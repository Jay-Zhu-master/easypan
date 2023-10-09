package com.jayzhu.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月09日 15:02:07
 * @packageName com.jayzhu.easypan.entity.enums
 * @className UploadStatusEnum
 * @describe TODO
 */
@AllArgsConstructor
@Getter
public enum UploadStatusEnum {
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading","上传中"),
    UPLOAD_FINISH("upload_finish","上传完成");
    private String code;
    private String desc;
}
