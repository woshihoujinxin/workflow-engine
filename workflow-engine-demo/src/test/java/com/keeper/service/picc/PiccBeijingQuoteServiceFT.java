package com.keeper.service.picc;

import com.keeper.service.base.PiccQuoteServiceFT;
import com.workflow.engine.core.service.entity.vo.AreaVO;
import org.databene.benerator.anno.InvocationCount;
import org.databene.benerator.anno.Offset;
import org.databene.benerator.anno.Source;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by houjinxin on 16/4/13.
 */
public class PiccBeijingQuoteServiceFT extends PiccQuoteServiceFT {

    @Override
    public AreaVO getArea() {
        return new AreaVO() {{
            setCityCode("110100");
            setName("北京市");
            setShortCode("京");
        }};
    }

    @Test
    @Source(value = "北京测试车辆.xlsx")
    @Offset(1)
    @InvocationCount(1) //限制测试方法执行次数
    public void testBeijingQuote(String index, final String licenseNo, final String carOwner, final String frameNo, final String engineNo, final String brandCode, final String enrollDate, final String idNo) throws ParseException {
        doTest(getArea(), index, licenseNo, carOwner, frameNo, engineNo, brandCode, enrollDate, idNo);
    }

}
