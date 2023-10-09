package com.jayzhu.easypan.entity.enums;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月09日 10:36:53
 * @packageName com.jayzhu.easypan.entity.enums
 * @className FileStatusEnum
 * @describe TODO
 */
public enum FileStatusEnum {
    TRANSFER(0,"转换中"),
    TRANSFER_FAIL(1,"转码失败"),
    USING(2,"使用中");
    private Integer status;
    private String desc;

    FileStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
