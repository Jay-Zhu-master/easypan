package com.jayzhu.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileFolderTypeEnum {

    FILE(0, "文件"),
    FOLDER(1, "文件夹");
    private final Integer type;
    private final String desc;
}
