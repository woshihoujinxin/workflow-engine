package com.workflow.engine.core.pingan.config;

/**
 * 业务场景常量定义
 * Created by houjinxin on 16/3/11.
 */
public class Constants {
    //响应成功,广义的成功,包含转保的情况
    public static final String _RESULT_CODE_C0000 = "C0000";
    //续保
    public static final String _RESULT_CODE_C0001 = "C0001";
    public static final String _RESULT_CODE_C0002 = "C0002";
    public static final String _RESULT_CODE_C0003 = "C0003";
    public static final String _RESULT_CODE_C0006 = "C0006";
    //车型无法找到
    public static final String _RESULT_CODE_C2003 = "C2003";
    //商业险提前投保
    public static final String _RESULT_CODE_C3003 = "C3003";
    //过户车
    public static final String _RESULT_CODE_C3009 = "C3009";
    //交强险未到投保日期报价失败
    public static final String _RESULT_CODE_C4003 = "C4003";
    //官网提示: 抱歉，您的车暂时无法继续投保，请咨询客服。
    public static final String _RESULT_CODE_S0001 = "S0001";
    //返回失败
    public static final String RETURN_RESULT_FAIL = "-1";
    //返回失败信息
    public static final String RETURN_RESULT_FAIL_MESSAGE = "报价返回失败，请重新尝试！";
    //供应商代码
    public static final String SUPPLIER_CODE = "pingan";
    //供应商名称
    public static final String SUPPLIER_NAME = "平安车险";

    public static final String[][] _NECESSARY_INFO_MAPPINGS = {
//            {"机动车辆损失险", "bizConfig.amount01", "damage"},
            {"盗抢险", "bizConfig.amount03", "theft"},
            {"自燃损失险", "bizConfig.amount18", "spontaneousCombustion"}
    };

    /**
     * 第一类险种映射关系,支持这种关系的城市有: 北京等
     * TODO: 记录支持该类型的城市
     * 每列的属性依次为: 构造报价结果时保费值的处理方式, 请求参数不同来源的不同处理方式, 响应中保额属性, 响应中保费属性, 请求中保额属性, 险种中文名称, 本系统中英文属性名
     * 构造报价结果(result)时保费值的处理方式,目前有两种:
     *    rType1: 报价响应中,险种保费不是多个险种报价的总和,例如:
     *         premium01 只代表车损的保费
     *    rType2: 险种保费是多个险种报价的总和,要经过计算得出每一项的保费 例如:
     *         premium49 代表车上人员司机和乘客的保费总和
     *         premium50 代表附加险(划痕,涉水,自燃)的总和
     * 构造报价请求参数(parameter)时不同类型参数的不同处理方式
     *    pType1: double2double 整形转整形 套餐中double型 -> 请求中 double型
     *    pType2: boolean2double 布尔转整形 套餐中boolean型 -> 请求中 double型
     *    pType3: booleanTo1or0 布尔转1或0 套餐中boolean型 -> 请求中 0或1
     *    pType4: glass,specialFactory 玻璃险,指定专修厂的处理
     *    pType5: none 不支持的险种, 保留以便扩展
     */
    public static final String[][] _TYPE1_ITEMS_MAPPINGS = {
            {"rType1", "pType3", "amount01", "premium01", "bizConfig.amount01", "机动车损失保险", "damage"},
            {"rType1", "pType1", "amount02", "premium02", "bizConfig.amount02", "第三者责任保险", "thirdParty"},
            {"rType1", "pType3", "amount03", "premium03", "bizConfig.amount03", "全车盗抢保险", "theft"},
            {"rType1", "pType1", "amount04", "premium04", "bizConfig.amount04", "车上人员责任险(司机)", "driver"},
            {"rType1", "pType1", "amount05", "premium05", "bizConfig.amount05", "车上人员责任险(乘客)", "passenger"},
            {"rType1", "pType4", "amount08", "premium08", "bizConfig.amount08", "玻璃单独破碎险", "glass"},
            {"rType1", "pType1", "amount17", "premium17", "bizConfig.amount17", "车身划痕损失险", "scratch"},
            {"rType1", "pType3", "amount18", "premium18", "bizConfig.amount18", "自燃损失险", "spontaneousCombustion"},
            {"rType1", "pType3", "amount27", "premium27", "bizConfig.amount27", "不计免赔险(车损)", "exemptDamage"},
            {"rType1", "pType3", "amount28", "premium28", "bizConfig.amount28", "不计免赔险(三者)", "exemptThirdParty"},
            {"rType1", "pType3", "amount41", "premium41", "bizConfig.amount41", "涉水行驶损失险", "engine"},
            {"rType1", "pType3", "amount48", "premium48", "bizConfig.amount48", "不计免赔险(盗抢)", "exemptTheft"},
            {"rType2", "pType3", "amount49", "premium49", "bizConfig.amount49", "不计免赔险(车上人员司机)", "exemptDriver"},
            {"rType2", "pType3", "amount49", "premium49", "bizConfig.amount49", "不计免赔险(车上人员乘客)", "exemptPassenger"},
            {"rType2", "pType3", "amount50", "premium50", "bizConfig.amount50", "不计免赔险(划痕险)", "exemptScratch"},
            {"rType2", "pType3", "amount50", "premium50", "bizConfig.amount50", "不计免赔险(涉水险)", "exemptEngine"},
            {"rType2", "pType3", "amount50", "premium50", "bizConfig.amount50", "不计免赔险(自燃险)", "exemptSpontaneousCombustion"},
            {"rType1", "pType4", "amount57", "premium57", "bizConfig.amount57", "指定专修厂特约险", "specialFactory"},
            {"rType1", "pType3", "amount59", "premium59", "bizConfig.amount59", "倒车镜、车灯单独损坏险", "rearView"},
            {"none", "pType5", "amount63", "premium63", "bizConfig.amount63", "机动车损失保险无法找到第三方特约险", "none"}
    };

    /**
     * 组合险种不计免赔属性名称与主险的属性名称以及insurancePackage的映射关系
     */
    public static final Object[][] _COMBINED_ITEM_MAPPINGS = {
            {"amount49", "premium49", new String[][]{
                    {"premium04", "driver", "exemptDriver", "不计免赔险(车上人员司机)"},
                    {"premium05", "passenger", "exemptPassenger", "不计免赔险(车上人员乘客)"}
                }
            },
            {"amount50", "premium50", new String[][]{
                    {"premium41", "engine", "exemptEngine", "不计免赔险(涉水险)"},
                    {"premium17", "scratch", "exemptScratch", "不计免赔险(划痕险)"},
                    {"premium18", "spontaneousCombustion", "exemptSpontaneousCombustion", "不计免赔险(自燃险)"}
                }
            }
    };

    /**
     * 第二类险种映射关系,支持这种关系的城市有: 天津
     * TODO: 记录支持该类型的城市
     * 车上人员和附加险的不计免赔各项是拆开的, 所以不需要考虑捆绑到一起的险种的拆分
     */
    public static final String[][] _TYPE2_ITEMS_MAPPINGS = {
            {"rType1", "pType3",  "amount01", "premium01", "bizConfig.amount01", "机动车损失保险", "damage"},
            {"rType1", "pType1",  "amount02", "premium02", "bizConfig.amount02", "第三者责任保险", "thirdParty"},
            {"rType1", "pType2",  "amount03", "premium03", "bizConfig.amount03", "全车盗抢保险", "theft"},
            {"rType1", "pType1",  "amount04", "premium04", "bizConfig.amount04", "车上人员责任险(司机)", "driver"},
            {"rType1", "pType1",  "amount05", "premium05", "bizConfig.amount05", "车上人员责任险(乘客)", "passenger"},
            {"rType1", "pType4",  "amount08", "premium08", "bizConfig.amount08", "玻璃单独破碎险", "glass"},
            {"rType1", "pType1",  "amount17", "premium17", "bizConfig.amount17", "车身划痕损失险", "scratch"},
            {"rType1", "pType2",  "amount18", "premium18", "bizConfig.amount18", "自燃损失险", "spontaneousCombustion"},
            {"rType1", "pType3",  "amount27", "premium27", "bizConfig.amount27", "不计免赔险(车损)", "exemptDamage"},
            {"rType1", "pType3",  "amount28", "premium28", "bizConfig.amount28", "不计免赔险(三者)", "exemptThirdParty"},
            {"rType1", "pType3",  "amount41", "premium41", "bizConfig.amount41", "涉水行驶损失险", "engine"},
            {"rType1", "pType3",  "amount48", "premium48", "bizConfig.amount48", "不计免赔险(盗抢)", "exemptTheft"},
            {"rType1", "pType3",  "amount49", "premium49", "bizConfig.amount49", "不计免赔险(车上人员司机)", "exemptDriver"},
            {"rType1", "pType4",  "amount57", "premium57", "bizConfig.amount57", "指定专修厂特约险", "specialFactory"}, //与玻璃类似 0 1 2
            {"rType1", "pType3",  "amount59", "premium59", "bizConfig.amount59", "倒车镜、车灯单独损坏险", "rearView"},
            {"rType1", "pType3",  "amount75", "premium75", "bizConfig.amount75", "不计免赔险(划痕险)", "exemptScratch"},
            {"rType1", "pType3",  "amount77", "premium77", "bizConfig.amount77", "不计免赔险(自燃险)", "exemptSpontaneousCombustion"},
            {"rType1", "pType3",  "amount79", "premium79", "bizConfig.amount79", "不计免赔险(涉水险)", "exemptEngine"},
            {"rType1", "pType3",  "amount80", "premium80", "bizConfig.amount80", "不计免赔险(车上人员乘客)", "exemptPassenger"},
            {"none", "pType5", "amount63", "premium63", "bizConfig.amount63", "机动车损失保险无法找到第三方特约险", "none"}
    };
}
