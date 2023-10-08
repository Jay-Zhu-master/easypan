package com.jayzhu.easypan.mapper;

import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.FileInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 文件信息表 Mapper 接口
 * </p>
 *
 * @author jayzhu
 * @since 2023-10-07
 */

@Mapper
@Repository
public interface FileInfoMapper {

    List<FileInfoVo> selectByQuery(FileInfoQuery query);
}
