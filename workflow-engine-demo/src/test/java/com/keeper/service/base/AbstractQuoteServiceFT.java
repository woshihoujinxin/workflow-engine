package com.keeper.service.base;

import com.keeper.service.MyFeeder;
import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.service.QuoteService;
import com.workflow.engine.core.service.entity.vo.AreaVO;
import com.workflow.engine.core.service.entity.vo.AutoVO;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.service.entity.vo.SupplierVO;
import com.workflow.engine.core.service.entity.vo.WangXiaoVO;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

/**
 * 批量测试
 * Created by houjinxin on 16/4/12.
 */

@RunWith(MyFeeder.class)
@ContextConfiguration(locations = {"classpath:spring-service.xml"})
@Transactional
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
public abstract class AbstractQuoteServiceFT {

    @Resource
    public ThreadPoolTaskExecutor taskExecutor;
    @Resource
    public QuoteService quoteService;
    public WangXiaoVO vo;

    public abstract List<SupplierVO> getSuppliers();
    public abstract AreaVO getArea();

    @Before
    public void setup() {
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        vo = new WangXiaoVO() {{
            setInsurancePackage(new InsurancePackageVO() {{
                setAutoTax(true);
                setCompulsory(true);
                setThirdParty(2000000);
                setExemptThirdParty(true);
                setDamage(true);
                setExemptDamage(true);
                setDriver(100000);
                setExemptDriver(true);
                setPassenger(100000);
                setExemptPassenger(true);
                setEngine(true);
                setExemptEngine(true);
                setScratch(20000);
                setExemptScratch(true);
                setTheft(true);
                setExemptTheft(true);
                setGlass("1");
                setSpontaneousCombustion(true);
                setExemptSpontaneousCombustion(true);
                setSpecialFactory("1");
                setRearView(true);
                setType("1");
            }});
        }};
        vo.setSuppliers(getSuppliers());
    }

    public void doTest(final AreaVO areaVO, String index, final String licenseNo, final String carOwner, final String frameNo, final String engineNo, final String brandCode, final String enrollDate, final String idNo) throws ParseException {
        WangXiaoVO vo = this.vo;
        vo.setCityCode(areaVO.getCityCode());
        vo.setAuto(new AutoVO() {{
            setArea(areaVO);
            setCarOwner(carOwner);
            setEngineNo(engineNo);
            setFrameNo(frameNo);
            setIdentity(idNo);
            setLicenseNo(licenseNo);
            setBrandCode(brandCode);
            setEnrollDate(DateUtils.parse(enrollDate, "yyyy-MM-dd"));
        }});
        System.out.println(vo);
        String businessId = quoteService.quote(vo);
        System.out.println("businessID===========" + businessId);
    }
}
