package com.workflow.engine.core.service.factory;

import com.workflow.engine.core.picc.service.PiccService;
import com.workflow.engine.core.pingan.service.PinganService;
import com.workflow.engine.core.service.IThirdPartyService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 三方服务工厂类
 * Created by houjinxin on 16/3/18.
 */
@Component
public class ThirdPartyServiceFactory {

    private Map<String, IThirdPartyService> thirdPartyServices = new HashMap<String, IThirdPartyService>(){{
        put("picc", new PiccService());
        put("pingan", new PinganService());
    }};

    /**
     * 按照服务名称获取对应的服务类
     * @param supplierCode
     * @return
     */
    public IThirdPartyService getServiceBySuppierCode(String supplierCode){
        for (Map.Entry<String, IThirdPartyService> thirdPartyServiceEntry: thirdPartyServices.entrySet()) {
            if(thirdPartyServiceEntry.getKey().contains(supplierCode)){
                return thirdPartyServiceEntry.getValue();
            }
        }
        throw new RuntimeException("找不到供应商对应的服务类");
    }

    public Map<String, IThirdPartyService> getThirdPartyServices() {
        return thirdPartyServices;
    }

}
