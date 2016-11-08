/**
 * 三方服务抽象基类
 * 下面列出了可以固化的上下文参数及意义,只列出重要的key:
 * <pre>
 *      baseContext(基础上下文):
 *         cityQuoteFlowMapping : 城市与报价流程映射
 *         cityFindVehicleFlowMapping : 城市与查车型流程的映射(一般为1个)
 *         stepCityPhMapping : 步骤城市请求参数处理映射
 *         stepCityRhMapping : 步骤城市响应处理映射
 *         cityCodeMapping : 城市代码映射
 *      bizContext(业务上下文):
 *         serviceName : 服务类简单类名,用于判断服务商名称
 *         applicantName : 申请人姓名
 *         applicantPhone : 申请人手机
 *         applicantEmail : 申请人邮件
 *         licenseNo : 车牌号
 *         engineNo : 发动机号
 *         carOwner : 车主姓名
 *         frameNo : 车架号
 *         enrollDate : 初登日期
 *         identity : 身份证信息
 *         brandCode : 车辆品牌型号
 *         bizStartDate : 商业险起保日期
 *         bizEndDate : 商业险中报日期
 *         forceStartDate : 交强险起保日期
 *         forceEndDate : 交强险终保日期
 *         area : 区域信息,包含cityCode和cityName等
 *         cityIsBeijing : 判断当前报价城市是否是北京, 北京不管是否是续保都要查询车型
 *         cityAreaCode : 城市代码
 *         insurancePackage : 套餐包
 *         flowType : 流程类型(报价,查找车型)
 *         isRenewal : 是否是续保用户. 在Picc是String类型,可选值为0,1,2;在Pingan是boolean类型
 *         vehicleInfo : 车型信息,结构应该为一个Map
 *         enabledInsuranceList : 可投保套餐列表
 *         disabledInsuranceList : 不可选套餐列表
 *
 *         bizCanApply : 商业险是否可投 true|false
 *         forceCanApply : 交强险是否可投保 true|false
 *         earliestForceBeginDate : 交强险最早投保日期
 *         earliestBizBeginDate : 商业险最早投保日期
 *         bizFailure : 商业险报价失败原因 MsgEnum类型
 *         forceFailure : 交强险报价失败原因 MsgEnum类型
 *
 *         result : 代表期望的结果类型
 *         stepInfo : 代表步骤返回值
 * </pre>
 * 各个保险公司也可以在上下文中加入自己的私有属性.
 * <pre>
 *     picc:
 *         reuseCarData : 车型数据是否可以复用 true-是,false-否
 *         interimNo : 核保通过后返回的一个序列,用于后续步骤中的请求参数.例如:"ZDAT2016000000000000000008189844"
 *     pingan:
 *         hasTax : 车价格是否含税
 *         forcePremium : 交强险报价详情
 *         bizPremium : 商业险报价详情
 *         vehicles : 车型列表
 *         transferYesOrNo : 是否过户车 false-0-否  true-1-是
 *         seatCountIsZero : 判定车座数是否为0
 * </pre>
 * Created by houjinxin on 16/4/19.
 */
package com.workflow.engine.core.service;