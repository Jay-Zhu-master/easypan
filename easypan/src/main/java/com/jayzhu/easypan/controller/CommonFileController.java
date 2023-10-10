package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.utils.FileReaderUtils;
import com.jayzhu.easypan.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

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
    AppConfig appConfig;

    public void getImage(HttpServletResponse response, String imageFolder, String imageName) {
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
}
