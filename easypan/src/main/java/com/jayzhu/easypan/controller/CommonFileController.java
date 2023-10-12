package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.component.RedisComponent;
import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.DownloadFileDto;
import com.jayzhu.easypan.entity.enums.FileCategoryEnum;
import com.jayzhu.easypan.entity.enums.FileFolderTypeEnum;
import com.jayzhu.easypan.entity.enums.ResponseCodeEnum;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.FileInfoVo;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.service.FileInfoService;
import com.jayzhu.easypan.utils.CopyTools;
import com.jayzhu.easypan.utils.FileReaderUtils;
import com.jayzhu.easypan.utils.StringTools;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月10日 15:42:46
 * @packageName com.jayzhu.easypan.controller
 * @className CommonFileController
 * @describe TODO
 */
public class CommonFileController extends ABaseController {
    @Resource
    private AppConfig appConfig;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private RedisComponent redisComponent;

    protected void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringTools.isEmpty(imageName) || !StringTools.pathIsOk(imageFolder) || !StringTools.pathIsOk(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        FileReaderUtils.readFile(response, filePath);
    }

    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;
        if (fileId.endsWith(".ts")) {
            String realFileId = fileId.split("_")[0];
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            String fileName = fileInfo.getFilePath();
            fileName = StringTools.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileName;
        } else {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return;
            }
            if (FileCategoryEnum.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8_NAME;
            } else {
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
        }
        FileReaderUtils.readFile(response, filePath);
    }

    protected ResponseVO getFolderInfo(String path, String userId) {
        String[] pathArray = path.split("/");
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        query.setFileIdArray(pathArray);
        query.setOrderBy("field(file_id,'" + StringUtils.join(pathArray, "','") + "')");
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(query);

        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FileInfoVo.class));
    }

    protected ResponseVO createDownloadUrl(String fileId, String userId) {
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
        if (fileInfo == null || fileInfo.getFolderType().equals(FileFolderTypeEnum.FOLDER.getType())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String code = StringTools.getRandomString(Constants.LENGTH_50);
        DownloadFileDto fileDto = new DownloadFileDto();
        fileDto.setDownloadCode(code);
        fileDto.setFilePath(fileInfo.getFilePath());
        fileDto.setFileName(fileInfo.getFileName());
        redisComponent.saveDownloadCode(fileDto);
        return getSuccessResponseVO(code);
    }

    protected void download(HttpServletRequest request, HttpServletResponse response, String code) throws Exception {
        DownloadFileDto downloadFileDto = redisComponent.getDownloadCode(code);
        if (downloadFileDto == null) {
            return;
        }
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + downloadFileDto.getFilePath();
        String fileName = downloadFileDto.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) { //ie浏览器
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        } else {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        FileReaderUtils.readFile(response,filePath);
    }
}
