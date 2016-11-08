package com.workflow.engine.core.common.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);
    private static PropertiesConfiguration config = null;

    static {
        try {
            config = new PropertiesConfiguration("all.properties");
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (Exception e) {
            log.error("ConfiUtils", e);
        }
    }

    public static String getStringByKey(String key) {
        return config.getString(key);
    }
}
