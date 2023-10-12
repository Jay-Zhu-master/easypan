package com.jayzhu.easypan.entity.dto;

import lombok.Data;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月12日 09:45:21
 * @packageName com.jayzhu.easypan.entity.dto
 * @className DownloadFileDto
 */

@Data
public class DownloadFileDto {
    private String downloadCode;
    private String fileName;
    private String filePath;
}

