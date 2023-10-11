package com.jayzhu.easypan.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月10日 11:20:52
 * @packageName com.jayzhu.easypan.utils
 * @className ScaleFilter
 * @describe TODO
 */
@Slf4j
public class ScaleFilter {
    public static void createCover4Video(File sourceFile, Integer width, File targetFile) {
        try {
            String cmd = "ffmpeg -i %s -y -vframes 1 -vf scale=%d/%d %s";
            ProcessUtils.executeCommand(String.format(cmd, sourceFile.getAbsolutePath(), width, width, targetFile.getAbsoluteFile()), false);
        } catch (Exception e) {
            log.error("生成视频封面失败", e);
        }
    }

    public static Boolean createThumbnailWithFfmpeg(File file, int thumbnailWidth, File targetFile, Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(file);
            int sorceW = src.getWidth();
            int sorceH = src.getHeight();
            if (sorceW <= thumbnailWidth) {
                return false;
            }
            compressImage(file, thumbnailWidth, targetFile, delSource);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public static void compressImage(File sourceFile, Integer width, File targeFile, Boolean delSource) {
        try {
            String cmd = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
            ProcessUtils.executeCommand(String.format(cmd, sourceFile.getAbsoluteFile(), width, targeFile.getAbsoluteFile()), true);
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
        } catch (Exception e) {
            log.error("压缩图片失败", e);
        }
    }
}
