package com.jayzhu.easypan.service.impl;

import com.jayzhu.easypan.component.RedisComponent;
import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.dto.UserSpaceDto;
import com.jayzhu.easypan.entity.enums.*;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.entity.po.UserInfo;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.query.SimplePage;
import com.jayzhu.easypan.entity.vo.PaginationResultVO;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.mapper.FileInfoMapper;
import com.jayzhu.easypan.mapper.UserInfoMapper;
import com.jayzhu.easypan.service.FileInfoService;
import com.jayzhu.easypan.utils.DateUtils;
import com.jayzhu.easypan.utils.ProcessUtils;
import com.jayzhu.easypan.utils.ScaleFilter;
import com.jayzhu.easypan.utils.StringTools;
import io.netty.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.bcel.Const;
import org.aspectj.util.FileUtil;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnum.UPLOADING.getCode());
                // 保存临时大小
                redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
                return resultDto;
            }
            // 最后一个分片上传完成，记录数据库，异步合并分片
            redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
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
            fileInfo.setFileName(fileName);
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setFilePid(filePid);
            fileInfo.setCreateTime(LocalDateTime.now());
            fileInfo.setLastUpdateTime(LocalDateTime.now());
            fileInfo.setFileCategory(fileTypeEnum.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnum.getType());
            fileInfo.setStatus(FileStatusEnum.TRANSFER.getStatus());
            fileInfo.setFolderType(FileFolderTypeEnum.FILE.getType());
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
     * @param webUserDto session中的用户信息
     * @param useSpace   使用空间
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

    /**
     * 分片文件转存
     *
     * @param fileId     文件id
     * @param webUserDto session中的用户信息
     */
    public void transferFile(String fileId, SessionWebUserDto webUserDto) {
        boolean transferSuccess = true;
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
            fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            if (FileTypeEnum.VIDEO == fileTypeEnum) {
                cutFile4Video(fileId, targetFilePath);
                // 生成缩略图
                cover = month + "/" + currentUserFolderName + Constants.IMAGE_PNG_SUFFIX;
                String coverPath = targetFolderName + "/" + cover;
                ScaleFilter.createCover4Video(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath));
            } else if (FileTypeEnum.IMAGE == fileTypeEnum) {
                // 生成缩略图
                cover = month + "/" + realFileName.replace(".", "_.");
                String coverPath = targetFolderName + "/" + cover;
                Boolean created = ScaleFilter.createThumbnailWithFfmpeg(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath), false);
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            log.error("文件转码失败，文件id：{}，userId:{}", fileId, webUserDto.getUserId(), e);
            transferSuccess = false;
        } finally {
            FileInfo updateFileInfo = new FileInfo();
            updateFileInfo.setFileSize(new File(targetFilePath).length());
            updateFileInfo.setFileCover(cover);
            updateFileInfo.setStatus(transferSuccess ? FileStatusEnum.USING.getStatus() : FileStatusEnum.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileStatusWithOldStatus(fileId, webUserDto.getUserId(), updateFileInfo, FileStatusEnum.TRANSFER.getStatus());
        }
    }

    /**
     * 合并分片文件方法
     *
     * @param dirPath    分片文件目录
     * @param toFilePath 目标文件目录
     * @param fileName   文件名
     * @param delSource  是否删除原文件
     */
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

    /**
     * 视频文件切片
     *
     * @param fileId        文件id
     * @param videoFilePath 文件路径
     */
    private void cutFile4Video(String fileId, String videoFilePath) {
        // 创建同名切片目录
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        //
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsFolder + "/" + Constants.TS_NAME;
        // 生成.ts
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, false);
        // 生成索引文件.m3u8 和切片.ts
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8_NAME, tsFolder.getPath(), fileId);
        ProcessUtils.executeCommand(cmd, false);
        // 删除index.ts
        new File(tsPath).delete();


    }

    /**
     * 新建文件夹
     *
     * @param filePid    文件父id
     * @param userId     用户id
     * @param folderName 文件夹名称
     * @return 文件夹信息
     */
    public FileInfo newFolder(String filePid, String userId, String folderName) {
        checkFileName(filePid, userId, folderName, FileFolderTypeEnum.FOLDER.getType());
        LocalDateTime now = LocalDateTime.now();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(StringTools.getRandomString(Constants.LENGTH_10));
        fileInfo.setUserId(userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setFileName(folderName);
        fileInfo.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        fileInfo.setCreateTime(now);
        fileInfo.setLastUpdateTime(now);
        fileInfo.setStatus(FileStatusEnum.USING.getStatus());
        fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfoMapper.insert(fileInfo);
        return fileInfo;
    }

    /**
     * 判断文件是否可用
     *
     * @param filePid    文件父id
     * @param userId     用户id
     * @param fileName   文件名
     * @param folderType 文件类型 文件/文件夹
     */
    private void checkFileName(String filePid, String userId, String fileName, Integer folderType) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFolderType(folderType);
        fileInfoQuery.setFileName(fileName);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        Integer count = fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            throw new BusinessException("此目录下已经存在同名文件请修改名称");
        }
    }

    /**
     * 文件重命名
     *
     * @param fileId   文件id
     * @param userId   用户id
     * @param fileName 新文件名
     * @return 重命名后文件信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo rename(String fileId, String userId, String fileName) {
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        String filePid = fileInfo.getFilePid();
        checkFileName(filePid, userId, fileName, fileInfo.getFolderType());
        if (FileFolderTypeEnum.FILE.getType().equals(fileInfo.getFolderType())) {
            fileName = fileName + StringTools.getFileSuffix(fileInfo.getFileName());
        }
        FileInfo dbInfo = new FileInfo();
        dbInfo.setFileName(fileName);
        dbInfo.setLastUpdateTime(LocalDateTime.now());
        fileInfoMapper.updateByFileIdAndUserId(dbInfo, fileId, userId);
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileName(fileName);
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        Integer count = fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 1) {
            throw new BusinessException("文件名" + fileName + "已存在");
        }
        fileInfo.setFileName(fileName);
        fileInfo.setLastUpdateTime(LocalDateTime.now());
        return fileInfo;
    }

    @Override
    public void changeFileFolder(String[] fileIds, String filePid, String userId) {
        if (Arrays.toString(fileIds).equals(filePid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (String.valueOf(Constants.ZERO).equals(filePid)) {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(filePid, userId);
            if (fileInfo == null || !FileDelFlagEnum.USING.getFlag().equals(fileInfo.getDelFlag())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        List<FileInfo> dbFileList = fileInfoService.findListByParam(fileInfoQuery);
        Map<String, FileInfo> dbFileNameMap = dbFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (file1, file2) -> file2));
        // 查询选中文件
        fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileIdArray(fileIds);
        List<FileInfo> selectFileList = fileInfoService.findListByParam(fileInfoQuery);

        // 将所选文件重命名
        for (FileInfo item : selectFileList) {
            FileInfo rootFileInfo = dbFileNameMap.get(item.getFileName());
            // 若文件名已经存在，则重命名被还原的文件名
            FileInfo updateInfo = new FileInfo();
            if (rootFileInfo != null) {
                String fileName = StringTools.rename(item.getFileName());
                updateInfo.setFileName(fileName);
            }
            updateInfo.setFilePid(filePid);
            this.fileInfoService.updateFileInfoByFileIdAndUserId(updateInfo, item.getFileId(), userId);
        }
    }

    @Override
    public void removeFile2RecycleBatch(String userId, String[] fileIds) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(fileIds);
        query.setDelFlag(FileDelFlagEnum.USING.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(query);
        if (fileInfoList.isEmpty()) {
            return;
        }
        List<String> delFilePidList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderFileList(delFilePidList, userId, fileInfo.getFileId(), FileDelFlagEnum.USING.getFlag());
        }
        if (!delFilePidList.isEmpty()) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setRecoveryTime(LocalDateTime.now());
            updateInfo.setDelFlag(FileDelFlagEnum.DEL.getFlag());
            fileInfoMapper.updateFileDelFlagBatch(updateInfo, userId, delFilePidList, null, FileDelFlagEnum.USING.getFlag());
        }
        // 将选中的文件更新为回收站
        List<String> delFileIdList = Arrays.asList(fileIds);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setRecoveryTime(LocalDateTime.now());
        fileInfo.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnum.USING.getFlag());
    }

    private void findAllSubFolderFileList(List<String> fileIdList, String userId, String fileId, Integer delFlag) {
        fileIdList.add(fileId);
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFilePid(fileId);
        query.setDelFlag(delFlag);
        query.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(query);
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderFileList(fileIdList, userId, fileInfo.getFileId(), delFlag);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoveryFileBatch(String userId, String[] fileIds) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(fileIds);
        query.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        List<String> delFileSubFolderFileList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            if (FileFolderTypeEnum.FOLDER.getType().equals(fileInfo.getFolderType())) {
                findAllSubFolderFileList(delFileSubFolderFileList, userId, fileInfo.getFileId(), FileDelFlagEnum.DEL.getFlag());
            }
        }
        // 查找根目录是否有同名文件
        query = new FileInfoQuery();
        query.setUserId(userId);
        query.setDelFlag(FileDelFlagEnum.USING.getFlag());
        query.setFilePid(String.valueOf(Constants.ZERO));
        List<FileInfo> allRootFileList = this.fileInfoMapper.selectList(query);

        Map<String, FileInfo> rootFileMap = allRootFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (file1, file2) -> file2));

        // 查询所有所选文件将目录下的所有删除的文件更新为正常
        if (!delFileSubFolderFileList.isEmpty()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
            this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, delFileSubFolderFileList, null, FileDelFlagEnum.DEL.getFlag());
        }
        // 将选中的文件更新为正常，且父级id设置为根目录

        List<String> delFileIdList = Arrays.asList(fileIds);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfo.setFilePid(String.valueOf(Constants.ZERO));
        fileInfo.setLastUpdateTime(LocalDateTime.now());
        fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnum.RECYCLE.getFlag());
        // 将所选文件重命名
        for (FileInfo item : fileInfoList) {
            FileInfo rootFileInfo = rootFileMap.get(item.getFileName());
            // 文件名重复
            if (rootFileInfo != null) {
                String fileName = StringTools.rename(item.getFileName());
                FileInfo updateInfo = new FileInfo();
                updateInfo.setFileName(fileName);
                fileInfoMapper.updateByFileIdAndUserId(updateInfo, item.getFileId(), userId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFileBatch(String userId, String[] fileIds, Boolean adminOp) {
        try {
            FileInfoQuery query = new FileInfoQuery();
            query.setUserId(userId);
            query.setFileIdArray(fileIds);
            query.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
            List<FileInfo> fileInfoList = fileInfoMapper.selectList(query);
            List<String> delFileSubFileFolderFileIdList = new ArrayList<>();
            // 找到所选文件子目录文件id
            for (FileInfo fileInfo : fileInfoList) {
                if (FileFolderTypeEnum.FOLDER.getType().equals(fileInfo.getFolderType())) {
                    findAllSubFolderFileList(delFileSubFileFolderFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnum.DEL.getFlag());
                }
            }
            // 删除所选文件子目录中的文件
            if (!delFileSubFileFolderFileIdList.isEmpty()) {
                fileInfoMapper.delFileBatch(userId, delFileSubFileFolderFileIdList, null, adminOp ? null : FileDelFlagEnum.DEL.getFlag());
            }
            // 删除所选文件
            fileInfoMapper.delFileBatch(userId, null, Arrays.asList(fileIds), adminOp ? null : FileDelFlagEnum.RECYCLE.getFlag());
//            delRealFile(userId, delFileSubFileFolderFileIdList, Arrays.asList(fileIds));
            // 退回用户空间
            Long useSpace = fileInfoMapper.selectUseSpace(userId);
            UserInfo userInfo = new UserInfo();
            userInfo.setUseSpace(useSpace);
            userInfoMapper.updateByUserId(userInfo, userId);
            // 更新缓存
            UserSpaceDto userSpaceDto = redisComponent.getUserSpaceUse(userId);
            userSpaceDto.setUseSpace(useSpace);
            redisComponent.saveUserSpaceUse(userId, userSpaceDto);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new BusinessException("文件删除失败");
        }
    }

    private void delRealFile(String userId, List<String> delFileSubFileFolderFileIdList, List<String> fileIds) throws IOException {
//        fileIds.addAll(delFileSubFileFolderFileIdList);
        FileInfoQuery query = new FileInfoQuery();
        query.setFilePidArray(delFileSubFileFolderFileIdList.toArray(new String[delFileSubFileFolderFileIdList.size()]));
//        query.setFolderType(FileFolderTypeEnum.FILE.getType());
        query.setUserId(userId);
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(query);
        fileInfoList.forEach(fileInfo -> {
            fileIds.add(fileInfo.getFileId());
        });
        query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(((String[]) fileIds.toArray()));
        fileInfoList = fileInfoMapper.selectList(query);
        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo.getFileType().equals(FileTypeEnum.IMAGE.getType())) {
                String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
                String compressImagePath = StringTools.getFileNameNoSuffix(filePath) + "_" + Constants.IMAGE_PNG_SUFFIX;
                File file = new File(filePath);
                FileUtils.forceDelete(file);
                file = new File(compressImagePath);
                FileUtils.forceDelete(file);
            } else if (fileInfo.getFileType().equals(FileTypeEnum.VIDEO.getType())) {
                String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
                String compressImagePath = StringTools.getFileNameNoSuffix(filePath) + Constants.IMAGE_PNG_SUFFIX;
                String tsFilePath = StringTools.getFileNameNoSuffix(filePath);
                File file = new File(filePath);
                FileUtils.forceDelete(file);
                file = new File(compressImagePath);
                FileUtils.forceDelete(file);
                file = new File(tsFilePath);
                FileUtils.forceDelete(file);
            } else {
                String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
                File file = new File(filePath);
                FileUtils.forceDelete(file);
            }
        }
    }
}
