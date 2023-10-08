package com.jayzhu.easypan.entity.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月08日 09:59:12
 * @packageName com.jayzhu.easypan.entity.query
 * @className FileInfoQuery
 * @describe TODO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoQuery {
    private String category;
    private String userId;
    private String filePid;
    private String fileNameFuzzy;
    private Integer pageNo;
    private Integer pageSize;
    private String orderBy;
    private Integer delFlag;
}
