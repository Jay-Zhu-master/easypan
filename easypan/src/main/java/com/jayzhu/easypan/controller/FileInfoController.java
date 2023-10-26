package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.annotation.GlobalInterceptor;
import com.jayzhu.easypan.annotation.VerifyParam;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UploadResultDto;
import com.jayzhu.easypan.entity.enums.FileFolderTypeEnum;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.FileInfoVo;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.entity.enums.FileCategoryEnum;
import com.jayzhu.easypan.entity.enums.FileDelFlagEnum;
import com.jayzhu.easypan.service.FileInfoService;
import com.jayzhu.easypan.utils.CopyTools;
import com.jayzhu.easypan.utils.StringTools;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
        FileCategoryEnum categoryEnum = FileCategoryEnum.getByCode(category);
        if (categoryEnum != null) {
            query.setFileCategory(categoryEnum.getCategory());
        }
        query.setUserId(((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY)).getUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnum.USING.getFlag());
        return getSuccessResponseVO(convert2PaginationVO(fileInfoService.findListByPage(query), FileInfoVo.class));
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
        return getSuccessResponseVO(resultDto);
    }

    @GetMapping("/getImage/{imageFolder}/{imageName}")
    @GlobalInterceptor
    public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    @GetMapping("/ts/getVideoInfo/{fileId}")
    @GlobalInterceptor
    public void getVideoInfo(HttpSession session, HttpServletResponse response, @PathVariable("fileId") String fileId) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    @RequestMapping("/getFile/{fileId}")
    @GlobalInterceptor
    public void getFile(HttpSession session, HttpServletResponse response, @PathVariable("fileId") String fileId) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    @PostMapping("/newFolder")
    @GlobalInterceptor
    public ResponseVO newFolder(HttpSession session, @RequestParam("filePid") String filePid, @RequestParam("fileName") String fileName) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVo.class));
    }

    @PostMapping("/getFolderInfo")
    @GlobalInterceptor
    public ResponseVO getFolderInfo(HttpSession session, @RequestParam("path") String path) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        return super.getFolderInfo(path, webUserDto.getUserId());
    }

    @PostMapping("/rename")
    @GlobalInterceptor
    public ResponseVO rename(HttpSession session,
                             @RequestParam("fileId") String fileId,
                             @RequestParam("fileName") String fileName) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        FileInfo fileInfo = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVo.class));
    }


    @PostMapping("/loadAllFolder")
    @GlobalInterceptor
    public ResponseVO loadAllFolder(HttpSession session,
                                    @RequestParam("filePid") String filePid,
                                    @RequestParam(value = "currentFileIds", required = false) String currentFileIds) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(webUserDto.getUserId());
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        if (!StringTools.isEmpty(currentFileIds)) {
            fileInfoQuery.setExcludeFileIdArray(currentFileIds.split(","));
        }
        fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
        fileInfoQuery.setOrderBy(" create_time desc");
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);
        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FileInfoVo.class));
    }

    @PostMapping("/changeFileFolder")
    @GlobalInterceptor
    public ResponseVO changeFileFolder(HttpSession session,
                                       @RequestParam("fileIds") String[] fileIds,
                                       @RequestParam("filePid") String filePid) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        fileInfoService.changeFileFolder(fileIds, filePid, webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/createDownloadUrl/{fileId}")
    @GlobalInterceptor
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @PathVariable("fileId") String fileId) {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        return super.createDownloadUrl(fileId, webUserDto.getUserId());
    }

    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false)
    public void download(HttpServletRequest request,
                         HttpServletResponse response,
                         @PathVariable("code") String code) throws Exception {
        super.download(request, response, code);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor(checkLogin = false)
    public ResponseVO delFile(HttpSession session,
                               @RequestParam("fileIds") String[] fileIds) throws Exception {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        fileInfoService.removeFile2RecycleBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }
}
