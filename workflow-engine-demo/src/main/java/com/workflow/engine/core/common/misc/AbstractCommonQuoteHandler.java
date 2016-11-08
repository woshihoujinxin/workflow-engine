package com.workflow.engine.core.common.misc;

import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO;

import java.util.Map;


/**
 * 抽象的Quote处理器,抽象出大部分的Quote构造时的处理逻辑.
 * <pre>
 * 1.构造Quote时,交强险和商业险分别处理,分别用了doOnForceCanApply和doOnBizCanApply两个方法.这两个方法封装的是当商业险和交强险可以投保时的报价结果的处理逻辑,以平安为例,这是交强险处理的方式:
 *           Map<String, String> forcePremium = (Map<String, String>) context.get("forcePremium");
 *           String forceBeginDate = (String) context.get("forceBeginDate");
 *           forceInfo.setStart_data(forceBeginDate);
 *           forceInfo.setPremium(forcePremium.get("totalPremium"));
 *           forceInfo.setTax(forcePremium.get("taxPremium"));
 *           forceInfo.setIs_success(true);
 *           ins_type.setForceInfo(forceInfo);
 *   从上下文中取出交强险的信息,赋值给forceInfo即可.
 * 2.构造Quote时,需要考虑这么几种情况:
 *    1)商业险可投,交强险不可投.
 *         WangXiaoResultVO中设置商业险的报价详情,交强险报价失败原因(一般为最早投保日期), 设置Code和Msg(MsgEnum.ForceFailure)
 *    2)交强险可投,商业险不可投.
 *         WangXiaoResultVO中设置交强险的报价详情,商业险报价失败原因, 设置Code和Msg为商业险失败时的Code和Msg(MsgEnum.BizFailure)
 *    3)都可以投.
 *         WangXiaoResultVO中设置交强险的报价详情,商业险报价详情, 设置默认的Code和Msg(MsgEnum.Default)
 *    4)都不可以投.
 *         WangXiaoResultVO中设置交强险的报价失败原因,商业险报价失败原因, 设置Code和Msg为全部失败时的Code和Msg(MsgEnum.BothFailure)
 * 3.在商业险和交强险报价的步骤中,会根据响应的状态来确定是否可以投保,在上下文中默认是都可以投保,若报价过程中发现不可投,会修改这两个键的值bizCanApply, forceCanApply
 *   在最后构造Quote时,才根据这两个值的来确定报价结果符合第二条中所描述的那种情况.最终最对应的处理
 * </pre>
 * 因各个保险公司差异造成的doOnForceCanApply和doOnBizCanApply不可能完全一样,故需要各自去实现商业险和交强险报价结果的处理
 * Created by houjinxin on 16/4/22.
 */
public abstract class AbstractCommonQuoteHandler {

    /**
     * Quote处理方法
     *
     * @param context
     * @param step
     * @param response
     * @param others
     * @return
     */
    public StepState handleQuote(Map<String, Object> context, IStep step, Object response, Object... others) {
        WangXiaoResultVO.Data.Quote quote = new WangXiaoResultVO.Data.Quote();
        WangXiaoResultVO.Data.Quote.InsType ins_type = new WangXiaoResultVO.Data.Quote.InsType();
        WangXiaoResultVO.Data.Quote.InsType.BizInfo bizInfo = new WangXiaoResultVO.Data.Quote.InsType.BizInfo();
        WangXiaoResultVO.Data.Quote.InsType.Force forceInfo = new WangXiaoResultVO.Data.Quote.InsType.Force();
        if (checkBothFailure(context)) { //商业险和交强险全都不可投
            quote.setCode(MsgEnum.BothFailure.getCode());
            quote.setMsg(MsgEnum.BothFailure.getMsg());
            handleBothFailure(context, ins_type, bizInfo, forceInfo);
        } else { //其中一个可投或者都可投
            MsgEnum msgEnum = context.get("bizFailure") != null ? //商业险报价失败,交强险报价成功
                    (MsgEnum) context.get("bizFailure") :
                    context.get("forceFailure") != null ? //商业险报价成功,交强险报价失败
                            (MsgEnum) context.get("forceFailure") :
                            MsgEnum.Default; //都成功
            quote.setCode(msgEnum.getCode());
            quote.setMsg(msgEnum.getMsg());
            //处理商业险报价结果
            handleBizInfo(context, ins_type, bizInfo);
            //处理交强险报价结果
            handleForceInfo(context, ins_type, forceInfo);
        }
        quote.setInsType(ins_type);
        //设置保险公司名称和代码
        BusinessUtil.setSupplierInfoForQuote((String)context.get("serviceName"), quote);
        context.put("result", quote);
        return BusinessUtil.successfulStepState(step);
    }

    /**
     * 确定商业险和交强险是否都不可投保
     *
     * @param context 上下文
     * @return 是否两种险种都不可投保
     */
    public boolean checkBothFailure(Map<String, Object> context) {
        boolean bizCanApply = (boolean) context.get("bizCanApply");
        boolean forceCanApply = (boolean) context.get("forceCanApply");
        return !bizCanApply && !forceCanApply;
    }

    /**
     * 交强险结果处理
     *
     * @param context 上下文
     * @param ins_type 报价结果载体
     * @param forceInfo 交强险报价结果载体
     */
    public void handleForceInfo(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.Force forceInfo) {
        boolean forceCanApply = (boolean) context.get("forceCanApply");
        if (forceCanApply) {
            forceInfo.setSuccess(true);
            forceInfo.setMessage("交强险报价成功");
            doOnForceCanApply(context, ins_type, forceInfo);
        } else {
            forceInfo.setSuccess(false);
            forceInfo.setMessage(((MsgEnum) context.get("forceFailure")).getMsg());
            ins_type.setForceInfo(forceInfo);
        }
    }

    /**
     * 商业险结果处理
     *
     * @param context 上下文
     * @param ins_type 报价结果载体
     * @param bizInfo 商业险报价结果载体
     */
    public void handleBizInfo(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.BizInfo bizInfo) {
        boolean bizCanApply = (boolean) context.get("bizCanApply");
        if (bizCanApply) {
            bizInfo.setSuccess(true);
            bizInfo.setMessage("商业险报价成功");
            doOnBizCanApply(context, ins_type, bizInfo);
        } else {
            bizInfo.setSuccess(false);
            bizInfo.setMessage(((MsgEnum) context.get("bizFailure")).getMsg());
            ins_type.setBizInfo(bizInfo);
        }
    }

    /**
     * 商业险和交强险都不可投的处理
     *
     * @param context 上下文
     * @param ins_type 报价结果载体
     * @param bizInfo 商业险报价结果载体
     * @param forceInfo 交强险报价结果载体
     */
    public void handleBothFailure(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.BizInfo bizInfo, WangXiaoResultVO.Data.Quote.InsType.Force forceInfo) {
        bizInfo.setSuccess(false);
        bizInfo.setMessage(((MsgEnum) context.get("bizFailure")).getMsg());
        forceInfo.setSuccess(false);
        forceInfo.setMessage(((MsgEnum) context.get("forceFailure")).getMsg());
        ins_type.setBizInfo(bizInfo);
        ins_type.setForceInfo(forceInfo);
    }

    /**
     * 商业险可投时, 从上下文中获取报价详情,填充到报价结果的BizInfo中
     *
     * @param context 上下文
     * @param ins_type 报价结果载体
     * @param bizInfo 商业险报价结果载体
     */
    protected abstract void doOnBizCanApply(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.BizInfo bizInfo);

    /**
     * 交强险可投时, 从上下文中获取报价详情,填充到报价结果的forceInfo中
     *
     * @param context 上下文
     * @param ins_type 报价结果载体
     * @param forceInfo 交强险报价结果载体
     */
    protected abstract void doOnForceCanApply(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.Force forceInfo);

}
