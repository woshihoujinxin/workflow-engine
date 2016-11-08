package com.workflow.engine.core.service.entity.po;

import java.io.Serializable;

/**
 * 渠道:暂定为IOS, ANDROID, WE_CHAT, WAP, PC, UNKNOWN
 * Created by houjinxin on 16/5/10.
 */
public class ChannelPO implements Serializable {

    private String id;
    private String name;
    private String description;

    public ChannelPO(){}

    public ChannelPO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {

        public static ChannelPO IOS;

        public static ChannelPO ANDROID;

        public static ChannelPO WE_CHAT;

        public static ChannelPO WAP;

        public static ChannelPO PC;

        public static ChannelPO UNKNOWN;// 未知

        public Enum() {
            IOS = new ChannelPO("IOS", "IOS相关设备");
            ANDROID = new ChannelPO("ANDROID", "ANDROID相关设备");
            WE_CHAT = new ChannelPO("WE_CHAT", "微信公众号");
            WAP = new ChannelPO("WAP", "手机WAP端");
            PC = new ChannelPO("PC", "PC版应用");
            UNKNOWN = new ChannelPO("UNKNOWN", "位置渠道");
        }

        public static ChannelPO getChannelByClientType(ClientType clientType) {
            if (ClientType.IOS.equals(clientType)) {
                return IOS;
            } else if (ClientType.ANDROID.equals(clientType)) {
                return ANDROID;
            } else if (ClientType.WE_CHAT.equals(clientType)) {
                return WE_CHAT;
            } else if (ClientType.WAP.equals(clientType)) {
                return WAP;
            } else if (ClientType.PC.equals(clientType)) {
                return PC;
            }
            return UNKNOWN;
        }

        public static ClientType getClientTypeByChannel(ChannelPO channel) {
            if (channel == null) {
                return null;
            } else if (PC.getId().equals(channel.getId())) {
                return ClientType.PC;
            } else if (IOS.getId().equals(channel.getId())) {
                return ClientType.IOS;
            } else if (ANDROID.getId().equals(channel.getId())) {
                return ClientType.ANDROID;
            } else if (WE_CHAT.getId().equals(channel.getId())) {
                return ClientType.WE_CHAT;
            } else if (WAP.getId().equals(channel.getId())) {
                return ClientType.WAP;
            }
            return null;
        }

    }
}

