package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.annotation.GlobalInterceptor;
import com.jayzhu.easypan.annotation.VerifyParam;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.FileInfoVo;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.entity.enums.FileCategoryEnum;
import com.jayzhu.easypan.entity.enums.FileDelFlagEnum;
import com.jayzhu.easypan.service.FileInfoService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件信息表 前端控制器
 * </p>
 *
 * @author jayzhu
 * @since 2023-10-07
 */
@RestController
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

    @Autowired
    FileInfoService fileInfoService;

    @PostMapping("/loadDataList")
    @GlobalInterceptor
    public ResponseVO loadDataList(HttpSession session, FileInfoQuery query) {
        FileCategoryEnum categoryEnum = FileCategoryEnum.getByCode(query.getFileCategory());
        if (categoryEnum != null) {
            query.setFileCategory(String.valueOf(categoryEnum.getCategory()));
        }
        query.setUserId(((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY)).getUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnum.USING.getFlag());
        return ResponseVO.success(convert2PaginationVO(fileInfoService.findListByPage(query), FileInfoVo.class));
    }

    @PostMapping("/uploadFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO uploadFile(HttpSession session,
                                 String fileId,
                                 MultipartFile file,
                                 @VerifyParam(required = true) String fileName,
                                 @VerifyParam(required = true) String filePid,
                                 @VerifyParam(required = true) String fileMd5,
                                 @VerifyParam(required = true) Integer chunkIndex,
                                 @VerifyParam(required = true) Integer chunks
    ) {
        SessionWebUserDto webUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        UploadResultDto resultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
        return ResponseVO.success(resultDto);
    }

    @GetMapping("/getImage/{imageFolder}/{imageName}")
    @GlobalInterceptor
    public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }
}
