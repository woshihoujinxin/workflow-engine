package com.workflow.engine.core.service.entity.po;


import org.apache.commons.lang.StringUtils;

/**
 * 客户端类型
 * Created by liqiang on 5/19/15.
 */
public enum ClientType {

    IOS,
    ANDROID,
    WE_CHAT,
    WAP,
    PC;

    public static ClientType convertStrToClientType(final String type) {
        if (StringUtils.isBlank(type))
            return null;

        for (ClientType clientType : ClientType.values()) {
            if (clientType.toString().equals(type)) {
                return clientType;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(convertStrToClientType("IOS"));
    }
}

