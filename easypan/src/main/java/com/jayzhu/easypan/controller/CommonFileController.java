package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.enums.FileCategoryEnum;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.service.FileInfoService;
import com.jayzhu.easypan.utils.FileReaderUtils;
import com.jayzhu.easypan.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileReader;

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
}
