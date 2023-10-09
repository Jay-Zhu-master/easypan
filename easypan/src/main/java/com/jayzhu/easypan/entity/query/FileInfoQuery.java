package com.jayzhu.easypan.entity.query;

import lombok.Data;

import java.util.Date;


/**
 * 文件信息参数
 */
@Data
public class FileInfoQuery extends BaseParam {
    /**
     * 文件ID
     */
    private String fileId;
    private String fileIdFuzzy;
    /**
     * 用户ID
     */
    private String userId;
    private String userIdFuzzy;
    /**
     * md5值，第一次上传记录
     */
    private String fileMd5;
    private String fileMd5Fuzzy;
    /**
     * 父级ID
     */
    private String filePid;
    private String filePidFuzzy;
    /**
     * 文件大小
     */
    private Long fileSize;
    /**
     * 文件名称
     */
    private String fileName;
    private String fileNameFuzzy;
    /**
     * 创建时间
     */
    private String createTime;
    private String createTimeStart;
    private String createTimeEnd;
    /**
     * 最后更新时间
     */
    private String lastUpdateTime;

    private String lastUpdateTimeStart;

    private String lastUpdateTimeEnd;

    /**
     * 0:文件 1:目录
     */
    private Integer folderType;

    private String FileCategory;

    private Integer delFlag;
    private Integer status;

}
