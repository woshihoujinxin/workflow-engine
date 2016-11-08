package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.AnnotationParser;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.misc.ILogger;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.utils.JsonValidatorUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.isNumeric;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil.generatorParamByStrategy;
import static com.workflow.engine.core.common.utils.StepUtil.generateParams;

/**
 * 步骤的抽象实现,这里定义了一个步骤中的所有操作,如下:
 * 1、构造请求参数
 * 2、发送请求
 * 3、处理结果
 * 其中每个环节都与业务逻辑相关,故真正的执行细节交给子类去实现。这里只做抽象。所有的需要发送请求以及处理响应的步骤都可以继承该抽象类
 * <p>
 * Created by houjinxin on 16/5/30.
 */
public abstract class AbstractStep implements IStep, ILogger {

    protected abstract String getRequestUrl();

    /**
     * 配置步骤所需参数列表,以及参数与参数获取方式的映射关系。
     *
     * @return
     */
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return null;
    };

    /**
     * 设置请求头, 根据需求在子类中设置需要的请求头,若不需要可直接返回null
     *
     * @return
     */
    protected abstract Map<String, String> getHeaders();

    /**
     * 根据需要确定发送的请求是post还是get请求。结果类型定义为Object是因为要处理两种响应,一种是String的,一种是HttpResponse的
     * String类型的可以直接获取到响应内容,进行处理。
     * HttpResponse类型的需要判断下Content-Type,根据内容类型是否是json来决定后续流程
     *
     * @param requestUrl
     * @param requestParams
     * @param headers
     * @return
     * @throws Exception
     */
    protected abstract Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception;

    /**
     * 当响应是json时的处理
     *
     * @param context
     * @param realResponse
     * @return
     */
    protected abstract StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception;

    /**
     * 当响应非json时的处理,通常是请求出现异常时的处理,也有非异常的情况,比如一个请求的响应是html页面,需要解析页面获取某些参数的情况
     *
     * @param context
     * @param realResponse
     * @return
     * @throws Exception
     */
    protected abstract StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception;

    /**
     * 有时需要在获取请求参数前在上下文中传入一些必要参数,以便在后续步骤中方便的获取,
     * 在有需要的情况下重写该方法,例如picc的GetSessionId步骤,先要获取到城市对应的cityCode和provinceCode,
     * 原本这两个参数也在上下文中,但是路径较深cityCodeMapping.{cityAreaCode}.cityCode,
     * cityCodeMapping.{cityAreaCode}.areaCode,cityAreaCode根据城市不同取值不同
     */
    protected void putNecessaryParamsIntoContext(Map<String, Object> context) {

    }

    /**
     * 有时需要在Http响应中获取响应头中的字段内容,可以在子类重写此方法
     *
     * @param context
     * @param response
     */
    protected void getNecessaryParamsFromHttpResponse(Map<String, Object> context, HttpResponse response) {

    }

    /**
     * 步骤执行前预先检查当前步骤是否需要执行或是直接跳过, 默认值当前步骤需要执行,故默认值是false。
     * 如交强险报价前,先检查交强险是否投保,若未投保则当前步骤直接跳过。故在交强险报价步骤中需要重写该方法。
     *
     * @param context
     * @return true-跳过, false-不跳过(默认)
     */
    protected boolean skipCurrentStepOrNot(Map<String, Object> context) {
        return false;
    }

    /**
     * 这个方法的目的是,解决不同的步骤中共用了许多相同的参数,造成为了构造请求参数,而产生大量的重复代码。因此如果在步骤中提供一个方法
     * stepNeededParamsMappings。在这里规定请求用到的所有参数(或者部分:对于商业险报价的请求参数可以通过追加到已经存在requestParams中来实现)
     * 以及参数的获取方式,参数获取策略等信息,那么就可以根据在上下文中已经注册的几种参数执行策略处理。
     *
     * 支持通过注解的方式配置参数生成策略,且注解配置优先,当注解不存在时,才会调用stepNeededParamsConfig方法获取配置
     * 如果两种方案都没获取参数配置那么将抛出异常
     *
     * @param context
     * @param step
     * @return
     * @throws Exception
     */
    protected Map<String, String> getRequestParams(Map<String, Object> context, IStep step) throws Exception {
        putNecessaryParamsIntoContext(context);
        ParamGeneratorConfig pgConfig;
        AnnotationParser parser = new AnnotationParser();
        pgConfig = parser.parserAnnotation(step.getClass());
        if (pgConfig.size() == 0) {
            pgConfig = stepNeededParamsConfig();
        }
        if (pgConfig == null) { //在方法中获取的pgConfig可能为空
            throw new RuntimeException("参数生成方案配置无效,请检查步骤【" + step.getClass().getSimpleName() + "】是否实现了stepNeededParamsConfig方法或配置有效的注解");
        }
        return generatorParamByStrategy(context, pgConfig, step);
    }

    /**
     * 判断是否要通过参数处理器来获得请求参数,针对有些请求的在不同地区需要不同参数的情况进行处理
     * 默认不需要通过参数处理器获得
     *
     * @return
     */
    protected boolean needParamsHandlerOrNot() {
        return false;
    }

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        getLogger().debug("当前步骤【{}】", this.getClass().getName());
        if (skipCurrentStepOrNot(context)) {
            getLogger().info("跳过当前步骤【{}】", this.getClass().getName());
            return successfulStepState(this);
        } else {
            Map<String, String> headers = getHeaders();
            Map<String, String> requestParams = needParamsHandlerOrNot()
                    ? generateParams(context, this)
                    : getRequestParams(context, this);
            Object response = sendRequest(getRequestUrl(), requestParams, headers);
            return handleResponse(context, response);
        }
    }

    /**
     * 如果请求的结果是字符串类型的可以直接处理;如果请求是HttpResponse 要判定响应的结果是json格式的,
     * 若不是则终止流程并在日志中记录
     *
     * @param context
     * @param response
     * @return
     * @throws Exception
     */
    protected StepState handleResponse(Map<String, Object> context, Object response) throws Exception {
        if (response instanceof String) {
            return doHandleJsonResponse(context, (String) response);
        } else {
            HttpResponse httpResponse = (HttpResponse) response;
            boolean isJson;
            String responseData;
            HttpEntity entity;
            getNecessaryParamsFromHttpResponse(context, httpResponse);
            //判断响应结果是否是json
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                entity = httpResponse.getEntity();
                responseData = JsonHelper.convert(EntityUtils.toString(entity, "utf-8"));
                //有时响应内容是json 有时是数字, 如果是json,可以通过响应头中的contentType进行判定。
                //有时响应头中的ContentType不是application/json 但是返回值确实是json所以这是要根据内容进行判断
                //也许是数字
                //TODO: 目前已知的响应内容类型为json html 数字,当发现新的类型的时候需要补充处理方法
                isJson = entity.getContentType().getValue().contains("application/json")
                        || (!isNumeric(responseData) && new JsonValidatorUtil().validate(responseData));
            } else {
                isJson = false;
                entity = httpResponse.getEntity();
                responseData = JsonHelper.convert(EntityUtils.toString(entity, "utf-8"));
            }
            if (isJson) {
                return doHandleJsonResponse(context, responseData);
            } else {
                return doHandleNotJsonResponse(context, responseData);
            }
        }
    }

}
