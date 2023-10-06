package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.annotation.GlobalInterceptor;
import com.jayzhu.easypan.annotation.VerifyParam;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.CreateImageCode;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.enums.VerifyRegexEnum;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.service.EmailCodeService;
import com.jayzhu.easypan.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("userInfoController")
@Slf4j
public class AccountController {


    @Autowired
    EmailCodeService emailCodeService;

    @Autowired
    UserInfoService userInfoService;

    /**
     * 获取验证码
     *
     * @param session
     * @param response
     * @param type
     * @throws IOException
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpSession session, HttpServletResponse response, Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
            log.info("验证码{},{}",Constants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
            log.info("验证码{},{}",Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) {
        try {

            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return ResponseVO.success(null);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true) String nickName,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return ResponseVO.success(null);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true) String email,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
            return ResponseVO.success(sessionWebUserDto);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }
}
