package com.workflow.engine.core.service;

/**
 * 第三方报价服务接口
 * Created by houjinxin on 16/3/9.
 */
public interface IThirdPartyService {


    /**
     * 报价接口
     * @param bizObject 业务对象
     */
    Object quote(Object bizObject);

    /**
     * 查询车型接口
     * @param bizObject
     * @return
     */
    Object findVehicle(Object bizObject);

}