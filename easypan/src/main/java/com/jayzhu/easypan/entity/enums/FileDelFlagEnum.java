package com.jayzhu.easypan.entity.enums;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月08日 10:20:22
 * @packageName com.jayzhu.easypan.enums
 * @className FileDelFlagEnums
 */
public enum FileDelFlagEnum {
    DEL(0, "删除"),
    RECYCLE(1, "回收站"),
    USING(2, "使用中");
    private Integer flag;
    private String desc;

    FileDelFlagEnum(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
