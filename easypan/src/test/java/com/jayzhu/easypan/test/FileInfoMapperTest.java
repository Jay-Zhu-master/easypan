package com.jayzhu.easypan.test;

import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.mapper.FileInfoMapper;
import com.jayzhu.easypan.service.FileInfoService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月08日 11:06:10
 * @packageName com.jayzhu.easypan.test
 * @className FileInfoMapperTest
 */

@SpringBootTest
public class FileInfoMapperTest {
    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private FileInfoService fileInfoService;

    @Test
    public void testSelectByQuery() {
        FileInfoQuery query = new FileInfoQuery();
        query.setPageNo(1);
        query.setPageSize(10);
        query.setOrderBy("last_update_time desc");
        fileInfoService.findListByPage(query);
    }
}
