package com.jayzhu.easypan.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月08日 10:11:56
 * @packageName com.jayzhu.easypan.entity.dto
 * @className FileInfoPageDto
 * @describe TODO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoVo {
    /**
     * 文件id
     */
    private String fileId;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件封面
     */
    private String fileCover;
    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime;

    /**
     * 0：文件 1：目录
     */
    private Boolean folderType;

    /**
     * 1：视频 2：音频 3：图片 4：pdf 5：doc 6：excel 7：txt 8：code 9：zip 10：其他
     */
    private Boolean fileType;

    /**
     * 0：转码中 1：转码失败 2：转码成功
     */
    private Boolean status;

    /**
     * 进入回收站时间
     */
    private LocalDateTime recoveryTime;

    /**
     * 0：删除 1：回收站 2：正常
     */
    private Boolean delFlag;

}
