package com.jayzhu.easypan.entity.enums;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月08日 10:00:28
 * @packageName com.jayzhu.easypan.enums
 * @className FileCategoryEnum
 */
public enum FileCategoryEnum {
//    ALL(0,"all","全部"),
    VIDEO(1, "video", "视频"),
    MUSIC(2, "music", "音频"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHERS(5, "others", "其它");
    private Integer category;
    private String code;
    private String desc;

    public static FileCategoryEnum getByCode(String code) {
        for (FileCategoryEnum item : FileCategoryEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    FileCategoryEnum(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }
}
