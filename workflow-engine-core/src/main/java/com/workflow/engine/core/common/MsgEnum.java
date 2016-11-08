package com.workflow.engine.core.common;

public enum MsgEnum {

    /**
     * 消息默认值,用于表示商业险和交强险全都报价成功
     */
    Default(1000, "商业险和交强险已成功报价"),
    /**
     * 商险报价失败
     */
    BizFailure(1001, ""),
    /**
     * 交强险报价失败
     */
    ForceFailure(1002, ""),

    /**
     * 商业险和交强险全部报价失败
     */
    BothFailure(1003, "商业险和交强险全部报价失败"),

    /**
     * 查询车型信息失败
     */
    QueryCar(1004, "查询车型信息失败"),

    /**
     * 查询车型信息失败 ，需要用户输入 （发动机号，车架号)
     */
    QueryCar_FrameNo(1005, "查询车型信息失败 ，需要用户补录信息"),

    /**
     * 车型信息 品牌、车座、车价等信息
     */
    QueryCar_VehicleInformation(1006, "返回车型信息"),

    /**
     * 查询车型信息失败 ，品牌型号无效
     */
    QueryCar_WrongBrandCode(1007, "查询车型信息失败 ，品牌型号无效"),

    /**
     * 您所选取的车型有误，请仔细核对您的行驶证原件后重新选择
     */
    QueryCar_WrongVehicleInfo(1008, "您所选取的车型有误，请仔细核对您的行驶证原件后重新选择"),

    /**
     * 车型出险三次以上，人工报价
     */
    OriginalMsg(1009, "车型出险三次以上，需要人工报价"),

    /**
     * 身份证号不正确
     */
    WrongIdentity(1010, "证件号不正确"),

    /**
     * 车座数不正确
     */
    SeatCountError(1011, "车座数不正确"),

    /**
     * 目前网站只支持家庭自用客车投保！
     */
    NonHomeCar(1012, "目前网站只支持家庭自用客车投保！"),

    /**
     * 暂不支持网上投保
     */
    CannotQuoteOnLine(1013, "该车暂不支持网上投保,建议联系客服人工报价!"),

    /**
     * 链接平台超时,请重试
     */
    TimeOut(1014, "链接平台超时,请重试!"),
    
    /**
     * 交强险重复投保或已投保交强险
     */
    ForceRepeat(1015,"交强险重复投保或已投保交强险"),

    /**
     * 其他未知原因报错,联系客服
     */
    Other(2000, "未知原因出错,建议联系客服人工报价!");

    private String msg;

    private int code;

    MsgEnum(int code, String msg) {

        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
