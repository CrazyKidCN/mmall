package com.crazykid.mmall.util;

import org.springframework.util.DigestUtils;

public class MD5Util {
    //加盐值可以增强密码被破解的难度
    private static String salt = "crazykid123!@#$%^&*()";

    public static String Encode(String origin) {
        return DigestUtils.md5DigestAsHex((MD5Util.salt+origin).getBytes());
    }
}
