package com.jayzhu.easypan.service;

import com.jayzhu.easypan.entity.dto.FileInfoPageDto;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 文件信息表 服务类
 * </p>
 *
 * @author jayzhu
 * @since 2023-10-07
 */
public interface FileInfoService {


    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

    /**
     * 根据条件查询列表
     */
    List<FileInfo> findListByParam(FileInfoQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(FileInfoQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    /**
     * 新增
     */
    Integer add(FileInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileInfo> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<FileInfo> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(FileInfo bean, FileInfoQuery param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(FileInfoQuery param);

    /**
     * 根据FileIdAndUserId查询对象
     */
    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);


    /**
     * 根据FileIdAndUserId修改
     */
    Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId);


    /**
     * 根据FileIdAndUserId删除
     */
    Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId);


    public void transferFile(String fileId, SessionWebUserDto webUserDto);

    FileInfo newFolder(String filePid, String userId, String folderName);

    FileInfo rename(String fileId, String userId, String fileName);

    void changeFileFolder(String[] fileIds, String filePid, String userId);

    /**
     * 删除文件到回收站
     *
     * @param userId  用户id
     * @param fileIds 文件id列表
     */
    void removeFile2RecycleBatch(String userId, String[] fileIds);

    /**
     * 批量从回收站恢复文件
     *
     * @param userId  用户id
     * @param fileIds 文件id列表
     */
    void recoveryFileBatch(String userId, String[] fileIds);

    void delFileBatch(String userId,String[] fileIds,Boolean adminOp);

}
