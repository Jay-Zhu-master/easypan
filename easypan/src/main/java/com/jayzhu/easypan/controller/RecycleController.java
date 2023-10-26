package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.annotation.GlobalInterceptor;
import com.jayzhu.easypan.annotation.VerifyParam;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.enums.FileDelFlagEnum;
import com.jayzhu.easypan.entity.po.FileInfo;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.FileInfoVo;
import com.jayzhu.easypan.entity.vo.PaginationResultVO;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.service.FileInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月12日 15:03:06
 * @packageName com.jayzhu.easypan.controller
 * @className RecycleController
 * @describe TODO
 */

@RestController
@RequestMapping("/recycle")
public class RecycleController extends ABaseController {
    @Resource
    private FileInfoService fileInfoService;

    /**
     * 获取回收站列表
     *
     * @param session  当前会话信息
     * @param pageNo   页码
     * @param pageSize 页面大小
     * @return 统一结果封装文件列表
     */
    @PostMapping("/loadRecycleList")
    @GlobalInterceptor
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setPageSize(pageSize);
        fileInfoQuery.setPageNo(pageNo);
        fileInfoQuery.setUserId(((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY)).getUserId());
        fileInfoQuery.setOrderBy("recovery_time desc");
        fileInfoQuery.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        PaginationResultVO<FileInfo> result = fileInfoService.findListByPage(fileInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVo.class));
    }

    @PostMapping("/recoverFile")
    @GlobalInterceptor
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String[] fileIds) {
        SessionWebUserDto webUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        fileInfoService.recoveryFileBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor(checkLogin = false)
    public ResponseVO delFile(HttpSession session,
                               @RequestParam("fileIds") String[] fileIds) throws Exception {
        SessionWebUserDto webUserDto = ((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY));
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds, false);
        return getSuccessResponseVO(null);
    }
}
