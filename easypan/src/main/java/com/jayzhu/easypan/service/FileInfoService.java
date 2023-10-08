package com.jayzhu.easypan.service;

import com.jayzhu.easypan.entity.dto.FileInfoPageDto;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件信息表 服务类
 * </p>
 *
 * @author jayzhu
 * @since 2023-10-07
 */
public interface FileInfoService {

    FileInfoPageDto findListByPage(FileInfoQuery query);

    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);
}
