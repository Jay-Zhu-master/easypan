package com.jayzhu.easypan.service.impl;

import com.jayzhu.easypan.component.RedisComponent;
import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.dto.UserSpaceDto;
import com.jayzhu.easypan.entity.enums.*;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.query.SimplePage;
import com.jayzhu.easypan.entity.vo.PaginationResultVO;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.mapper.FileInfoMapper;
import com.jayzhu.easypan.mapper.UserInfoMapper;
import com.jayzhu.easypan.service.FileInfoService;
import com.jayzhu.easypan.utils.DateUtils;
import com.jayzhu.easypan.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文件信息表 服务实现类
 * </p>
 *
 * @author jayzhu
 * @since 2023-10-07
 */
@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {

    @Autowired
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    @Lazy
    private FileInfoService fileInfoService;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileInfo> findListByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileInfo> list = this.findListByParam(param);
        PaginationResultVO<FileInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(FileInfo bean) {
        return this.fileInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.deleteByParam(param);
    }

    /**
     * 根据FileIdAndUserId获取对象
     */
    @Override
    public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
        return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    /**
     * 根据FileIdAndUserId修改
     */
    @Override
    public Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId) {
        return this.fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
    }

    /**
     * 根据FileIdAndUserId删除
     */
    @Override
    public Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId) {
        return this.fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
    }

    /**
     * 文件上传核心业务
     *
     * @param webUserDto session中的用户信息
     * @param fileId     文件id
     * @param file       文件
     * @param fileName   文件名
     * @param filePid    文件父id
     * @param fileMd5    文件md5值
     * @param chunkIndex 分片索引
     * @param chunks     分片片数
     * @return UploadResultDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file,
                                      String fileName, String filePid, String fileMd5, Integer chunkIndex,
                                      Integer chunks) {
        UploadResultDto resultDto = new UploadResultDto();
        Boolean uploadSuccess = true;
        File tempFileFolder = null;
        try {
            if (StringTools.isEmpty(fileId)) {
                fileId = StringTools.getRandomString(Constants.LENGTH_10);
            }
            resultDto.setFileId(fileId);
            UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
            if (chunkIndex == 0) {
                FileInfoQuery infoQuery = new FileInfoQuery();
                infoQuery.setFileMd5(fileMd5);
                infoQuery.setSimplePage(new SimplePage(0, 1));
                infoQuery.setStatus(FileStatusEnum.USING.getStatus());
                List<FileInfo> dbFileList = fileInfoMapper.selectList(infoQuery);
                // 秒传
                if (!dbFileList.isEmpty()) {
                    FileInfo dbFile = dbFileList.get(0);
                    // 判断文件大小
                    if (dbFile.getFileSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                        throw new BusinessException(ResponseCodeEnum.CODE_904);
                    }
                    dbFile.setFileId(fileId);
                    dbFile.setFilePid(filePid);
                    dbFile.setUserId(webUserDto.getUserId());
                    dbFile.setCreateTime(LocalDateTime.now());
                    dbFile.setLastUpdateTime(LocalDateTime.now());
                    dbFile.setStatus(FileStatusEnum.USING.getStatus());
                    dbFile.setDelFlag(FileDelFlagEnum.USING.getFlag());
                    dbFile.setFileMd5(fileMd5);

                    // 文件重命名
                    fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
                    dbFile.setFileName(fileName);
                    fileInfoMapper.insert(dbFile);
                    resultDto.setStatus(UploadStatusEnum.UPLOAD_SECONDS.getCode());
                    // 更新用户使用空间
                    updateUseSpace(webUserDto, dbFile.getFileSize());
                    return resultDto;
                }
            }
            // 正常上传
            // 判断磁盘空间
            Long currentTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            if (file.getSize() + currentTempSize + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }
            // 暂存临时目录
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = webUserDto.getUserId() + fileId;

            tempFileFolder = new File(tempFolderName + currentUserFolderName);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }
            File newFile = new File(tempFileFolder.getParent() + "/" + chunkIndex);
            file.transferTo(newFile);
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnum.UPLOADING.getCode());
                // 保存临时大小
                redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
                return resultDto;
            }
            // 最后一个分片上传完成，记录数据库，异步合并分片
            String month = DateUtils.format(LocalDateTime.now(), DateTimePatternEnum.YYYYMM.getPattern());
            String fileSuffix = StringTools.getFileSuffix(fileName);
            // 真实文件名
            String realFileName = currentUserFolderName + fileSuffix;
            FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            // 自动重命名
            fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setUserId(webUserDto.getUserId());
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setFilePid(filePid);
            fileInfo.setCreateTime(LocalDateTime.now());
            fileInfo.setLastUpdateTime(LocalDateTime.now());
            fileInfo.setFileCategory(fileTypeEnum.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnum.getType());
            fileInfo.setStatus(FileStatusEnum.TRANSFER.getStatus());
            fileInfo.setFolderType(FileFolderTypeEnum.FOLDER.getType());
            fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
            fileInfoMapper.insert(fileInfo);

            // 更新用户使用空间
            Long totalSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            updateUseSpace(webUserDto, totalSize);

            resultDto.setStatus(UploadStatusEnum.UPLOAD_FINISH.getCode());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(fileInfo.getFileId(), webUserDto);
                }
            });


            return resultDto;
        } catch (BusinessException e) {
            log.error("文件上传失败", e);
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            uploadSuccess = false;
        } finally {
            if (!uploadSuccess && tempFileFolder != null) {
                try {
                    FileUtils.deleteDirectory(tempFileFolder);
                } catch (IOException e) {
                    log.error("临时目录删除失败", e);
                }
            }
        }
        return resultDto;
    }

    /**
     * 私有方法，自动重命名文件
     *
     * @param filePid  文件父id
     * @param userId   用户id
     * @param fileName 文件名
     * @return 重命名后的文件名
     */
    private String autoRename(String filePid, String userId, String fileName) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfoQuery.setFileName(fileName);
        Integer count = fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            fileName = StringTools.rename(fileName);
        }
        return fileName;
    }

    /**
     * 私有方法，更新使用空间
     *
     * @param webUserDto
     * @param useSpace
     */
    private void updateUseSpace(SessionWebUserDto webUserDto, Long useSpace) {
        Integer count = userInfoMapper.updateUseSpace(webUserDto.getUserId(), useSpace, null);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_904);
        }
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
        spaceDto.setUseSpace(spaceDto.getUseSpace() + useSpace);
        redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);
    }

    public void transferFile(String fileId, SessionWebUserDto webUserDto) {
        Boolean transferSuccess = true;
        String targetFilePath = null;
        String cover = null;
        FileTypeEnum fileTypeEnum = null;
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, webUserDto.getUserId());
        try {
            if (fileInfo == null || !FileStatusEnum.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
                return;
            }
            // 找到临时目录
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String currentUserFolderName = webUserDto.getUserId() + fileId;
            File fileFolder = new File(tempFolderName + currentUserFolderName);
            String fileSuffix = StringTools.getFileSuffix(fileInfo.getFileName());
            String month = DateUtils.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
            String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File targetFolder = new File(targetFolderName + "/" + month);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            // 真实的文件名
            String realFileName = currentUserFolderName + fileSuffix;
            targetFilePath = targetFolder.getPath() + "/" + realFileName;
            //合并文件
            unionFile(fileFolder.getPath(), targetFilePath, fileInfo.getFileName(), true);
            // 视频文件切割
        } catch (Exception e) {
            log.error("文件转码失败，文件id：{}，userId:{}", fileId, webUserDto.getUserId(), e);
            transferSuccess = false;
        } finally {
            FileInfo updateFileInfo = new FileInfo();
            updateFileInfo.setFileSize(new File(targetFilePath).length());
            updateFileInfo.setFileCover(cover);
            updateFileInfo.setStatus(transferSuccess ? FileStatusEnum.USING.getStatus() : FileStatusEnum.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileStatusWithOldStatus(fileId,webUserDto.getUserId(),updateFileInfo,FileStatusEnum.TRANSFER.getStatus());
        }
    }

    private void unionFile(String dirPath, String toFilePath, String fileName, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        RandomAccessFile writeFile = null;
        try {
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                File chunkFile = new File(dirPath + "/" + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败", e);
                    throw new BusinessException("合并分片失败");
                } finally {
                    readFile.close();
                }
            }
        } catch (Exception e) {
            log.error("合并文件:{}失败", fileName, e);
            throw new BusinessException("合并文件" + fileName + "失败");
        } finally {
            if (writeFile != null) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    log.error("文件流关闭失败", e);
                }
            }
            if (delSource & dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    log.error("文件夹删除失败", e);
                }
            }
        }
    }
}
