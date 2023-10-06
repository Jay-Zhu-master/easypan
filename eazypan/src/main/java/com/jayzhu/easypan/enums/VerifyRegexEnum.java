package com.jayzhu.easypan.enums;

public enum VerifyRegexEnum {
    NO("", "不校验"),
    EMAIL("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$", "邮箱"),
    PASSWORD("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\\\W]{8,18}$", "只能是数字、字母、特殊字符 6-18位");

    private String regex;
    private String desc;

    VerifyRegexEnum(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

    public String getRegex() {
        return regex;
    }

    public String getDesc() {
        return desc;
    }
}
