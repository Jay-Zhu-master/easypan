package com.jayzhu.easypan.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jayzhu.easypan.entity.dto.FileInfoPageDto;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.FileInfoVo;
import com.jayzhu.easypan.mapper.FileInfoMapper;
import com.jayzhu.easypan.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public class FileInfoServiceImpl implements FileInfoService {

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    public FileInfoPageDto findListByPage(FileInfoQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<FileInfoVo> list = fileInfoMapper.selectByQuery(query);
        Page<FileInfoVo> page = ((Page<FileInfoVo>) list);
        FileInfoPageDto fileInfoPageDto = new FileInfoPageDto();
        fileInfoPageDto.setTotalCount(((int) page.getTotal()));
        fileInfoPageDto.setPageSize(page.getPageSize());
        fileInfoPageDto.setPageNo(page.getPageNum());
        fileInfoPageDto.setPageTotal(page.getPages());
        fileInfoPageDto.setList(page.getResult());
        return fileInfoPageDto;
    }

    @Override
    public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        return null;
    }
}
