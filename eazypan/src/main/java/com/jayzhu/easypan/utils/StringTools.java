package com.jayzhu.easypan.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class StringTools {
    private StringTools() {
    }

    /**
     * 生成随机数
     *
     * @param count
     * @return
     */
    public static String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str) || "null".equals(str) || "\u0000".equals(str) || "".equals(str.trim());
    }

    public static String encodeByMd5(String originSting) {
        return isEmpty(originSting) ? null : DigestUtils.md5Hex(originSting);
    }
}
