package com.jayzhu.easypan.entity.dto;

import com.jayzhu.easypan.entity.vo.FileInfoVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月08日 15:52:49
 * @packageName com.jayzhu.easypan.entity.dto
 * @className FileInfoPageBean
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoPageDto {
    private Integer totalCount;
    private Integer pageSize;
    private Integer pageNo;
    private Integer pageTotal;
    List<FileInfoVo> list;
}
