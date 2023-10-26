package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.annotation.GlobalInterceptor;
import com.jayzhu.easypan.annotation.VerifyParam;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.query.FileInfoQuery;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.service.FileInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/share")
public class ShareController extends ABaseController {


    @Resource
    FileInfoService fileInfoService;

    @PostMapping("/loadShareList")
    @GlobalInterceptor
    public ResponseVO loadShareList(HttpSession session,
                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(((SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY)).getUserId());
        fileInfoService.findListByPage(query);
        return getSuccessResponseVO(null);
    }
}
