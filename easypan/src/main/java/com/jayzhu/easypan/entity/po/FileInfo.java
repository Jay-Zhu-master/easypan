package com.jayzhu.easypan.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文件信息表
 * </p>
 *
 * @author jayzhu
 * @since 2023-10-07
 */
public class FileInfo implements Serializable {
    /**
     * 文件id
     */
    private String fileId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件md5值
     */
    private String fileMd5;

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
     * 文件路径
     */
    private String filePath;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime;

    public void setFolderType(Integer folderType) {
        this.folderType = folderType;
    }

    /**
     * 0：文件 1：目录
     */
    private Integer folderType;

    /**
     * 文件分类：1：视频 2：阴平 3：图片 4：文档 5：其他
     */
    private Integer fileCategory;

    /**
     * 1：视频 2：音频 3：图片 4：pdf 5：doc 6：excel 7：txt 8：code 9：zip 10：其他
     */
    private Integer fileType;

    /**
     * 0：转码中 1：转码失败 2：转码成功
     */
    private Integer status;

    /**
     * 进入回收站时间
     */
    private LocalDateTime recoveryTime;

    /**
     * 0：删除 1：回收站 2：正常
     */
    private Integer delFlag;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFilePid() {
        return filePid;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileCover() {
        return fileCover;
    }

    public void setFileCover(String fileCover) {
        this.fileCover = fileCover;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    public Integer getFileCategory() {
        return fileCategory;
    }
    public void setFileCategory(Integer fileCategory) {
        this.fileCategory = fileCategory;
    }
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getFolderType() {
        return folderType;
    }

    public Integer getFileType() {
        return fileType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(LocalDateTime recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileId = " + fileId +
                ", userId = " + userId +
                ", fileMd5 = " + fileMd5 +
                ", filePid = " + filePid +
                ", fileSize = " + fileSize +
                ", fileName = " + fileName +
                ", fileCover = " + fileCover +
                ", filePath = " + filePath +
                ", createTime = " + createTime +
                ", lastUpdateTime = " + lastUpdateTime +
                ", folderType = " + folderType +
                ", fileCategory = " + fileCategory +
                ", fileType = " + fileType +
                ", status = " + status +
                ", recoveryTime = " + recoveryTime +
                ", delFlag = " + delFlag +
                "}";
    }
}
