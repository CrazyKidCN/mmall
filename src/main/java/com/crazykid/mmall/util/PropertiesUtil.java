package com.crazykid.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 *  读取配置文件的工具类
 */
@Slf4j
public class PropertiesUtil {
    private static Properties props;

    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);
        }
    }

    public static String getProperties(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return value.trim();
    }

    public static String getProperties(String key, String defaultValue) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value.trim();
    }
}
