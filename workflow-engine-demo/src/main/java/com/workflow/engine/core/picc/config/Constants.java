package com.workflow.engine.core.picc.config;

/**
 * 业务场景常量定义
 * Created by houjinxin on 16/3/11.
 */
public class Constants {

    //供应商代码
    public static final String SUPPLIER_CODE = "picc";
    //供应商名称
    public static final String SUPPLIER_NAME = "人保";

    /**
     * 用来描述险种间的约束关系, 即如果数组第一个元素代表的险种被禁用,那么第二个数组中的所有元素代表的险种都要禁用
     */
    public static final Object[][] _ITEMS_RESTRICTION_RULL = {
            {"050200", new String[]{
                    "050310", "050231", "050270", "050210", "050252", "050291", "050911", "050922", "050924", "050451"
            }},
            {"050600", new String[]{
                    "050912", "050641", "050919"
            }},
            {"050500", new String[]{"050921"}},
            {"050701", new String[]{"050928", "050642"}}, //关系较复杂约束关系待定
            {"050702", new String[]{"050929", "050918"}}, //关系较复杂约束关系待定
            {"050291", new String[]{"050924"}},
            {"050210", new String[]{"050922"}},
            {"050641", new String[]{"050919"}},
            {"050642", new String[]{"050918"}},
            {"050643", new String[]{"050917"}},
    };

    /**
     * 这个映射的顺序不可以更改, 因商业险报价结果跟参数的顺序有密切关系,必须按照此数组的顺序处理,在选择数据结构存储时
     * 要考虑顺序的因素,要选择有序结合
     * type1: double2double 整形转整形
     * type2: boolean2double 布尔转整形
     * type3: booleanTo1or0 布尔转1或0
     * type4: glass 玻璃
     * type5: none 不支持的险种
     * type6: StringTo1or0 字符串转1或0
     */
    public static final String[][] _KIND_CODE_MAPPINGS = {
            {"type2", "050200", "机动车辆损失险", "damage"},
            {"type1", "050600", "第三者责任险", "thirdParty"},
            {"type2", "050500", "盗抢险", "theft"},
            {"type1", "050701", "车上人员责任险(司机)", "driver"},
            {"type1", "050702", "车上人员责任险(乘客)", "passenger"},
            {"type2", "050310", "自燃损失险", "spontaneousCombustion"},
            {"type4", "050231", "玻璃单独破碎险", "glass"},
            {"type5", "050270", "机动车停驶损失险", "none"},
            {"type1", "050210", "车身划痕损失险", "scratch"},
            {"type6", "050252", "指定修理厂特约条款", "specialFactory"},
            {"type3", "050291", "发动机特别损失险", "engine"},
            {"type3", "050911", "机动车辆损失险不计免赔", "exemptDamage"},
            {"type3", "050912", "第三者责任保险不计免赔", "exemptThirdParty"},
            {"type3", "050921", "盗抢险不计免赔", "exemptTheft"},
            {"type3", "050922", "车身划痕损失险不计免赔", "exemptScratch"},
            {"type3", "050924", "发动机特别损失险不计免赔", "exemptEngine"},
            {"type3", "050928", "车上人员责任险(司机)不计免赔", "exemptDriver"},
            {"type5", "050330", "车损险可选免赔额特约条款", "none"},
            {"type3", "050935", "自燃损失险不计免赔", "exemptSpontaneousCombustion"},
            {"type5", "050918", "精神损害抚慰金责任险（车上人员）不计免赔", "none"},
            {"type5", "050919", "精神损害抚慰金责任险（三者险）不计免赔", "none"},
            {"type5", "050917", "精神损害抚慰金责任险", "none"},
            {"type5", "050451", "机动车损失保险无法找到第三方特约险", "none"},
            {"type5", "050642", "精神损害抚慰金责任险（车上人员）", "none"},
            {"type5", "050641", "精神损害抚慰金责任险（三者险）", "none"},
            {"type5", "050643", "精神损害抚慰金责任险", "none"},
            {"type3", "050929", "车上人员责任险(乘客)不计免赔", "exemptPassenger"}
    };

    public static final String[][] _NECESSARY_INFO_MAPPINGS = {
            {"机动车辆损失险", "050200", "damage"},
            {"盗抢险", "050500", "theft"},
            {"自燃损失险", "050310", "spontaneousCombustion"}
    };
}
