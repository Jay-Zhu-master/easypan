package com.jayzhu.easypan.controller;

import com.jayzhu.easypan.annotation.GlobalInterceptor;
import com.jayzhu.easypan.annotation.VerifyParam;
import com.jayzhu.easypan.component.RedisComponent;
import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.CreateImageCode;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.UserSpaceDto;
import com.jayzhu.easypan.entity.po.UserInfo;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.entity.enums.VerifyRegexEnum;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.service.EmailCodeService;
import com.jayzhu.easypan.service.UserInfoService;
import com.jayzhu.easypan.utils.FileReaderUtils;
import com.jayzhu.easypan.utils.StringTools;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController("userInfoController")
@Slf4j
public class AccountController extends ABaseController {


    @Autowired
    EmailCodeService emailCodeService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    RedisComponent redisComponent;

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
            log.info("验证码{},{}", Constants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
            log.info("验证码{},{}", Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    /**
     * 发送邮箱验证码
     *
     * @param session
     * @param email
     * @param checkCode
     * @param type
     * @return
     */
    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) {
        try {

            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    /**
     * 用户注册
     *
     * @param session
     * @param email
     * @param nickName
     * @param password
     * @param checkCode
     * @param emailCode
     * @return
     */
    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
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
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @param session:
     * @param email:
     * @param password:
     * @param checkCode:
     * @return com.jayzhu.easypan.entity.vo.ResponseVO
     * @description: 用户登陆
     * @author: jayzhu
     * @date: 2023/10/7 10:28
     */
    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
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
            return getSuccessResponseVO(sessionWebUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @param session:
     * @param email:
     * @param password:
     * @param checkCode:
     * @param emailCode:
     * @return com.jayzhu.easypan.entity.vo.ResponseVO
     * @description: 重置密码
     * @author: jayzhu
     * @date: 2023/10/7 10:54
     */
    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getAvatar(HttpServletResponse response, @PathVariable(value = "userId") String userId) {
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        if (!file.exists()) {
            if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
                FileReaderUtils.printNoDefaultImage(response);
            }
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
        }
        response.setContentType("image/jpg");
        FileReaderUtils.readFile(response, avatarPath);
    }

    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getUserInfo(HttpSession session) {
        return getSuccessResponseVO(userInfoService.getUserInfoFromSession(session));
    }

    @RequestMapping("/getUseSpace")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = userInfoService.getUserInfoFromSession(session);
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId());
        return getSuccessResponseVO(spaceDto);
    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    @PostMapping("/updateUserAvatar")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updateUserAvatar(HttpSession session, @RequestParam("avatar") MultipartFile avatar) throws IOException {
        SessionWebUserDto sessionWebUserDto = userInfoService.getUserInfoFromSession(session);
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }
        File targetFile = new File(targetFileFolder.getPath() + "/" + sessionWebUserDto.getUserId() + Constants.AVATAR_SUFFIX);
        try {
            avatar.transferTo(targetFile);
        } catch (Exception e) {
            log.error("上传头像失败", e);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setQqAvatar("");
        userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
        sessionWebUserDto.setAvatar(null);
        session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updatePassword(HttpSession session,
                                     @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringTools.encodeByMd5(password));
        userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
        return getSuccessResponseVO(null);
    }


    @PostMapping("/qqlogin")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO qqlogin(HttpSession session, String callbackUrl) throws UnsupportedEncodingException {
        String state = StringTools.getRandomNumber(Constants.LENGTH_30);
        if (!StringTools.isEmpty(callbackUrl)) {
            session.setAttribute(state, callbackUrl);
        }
        String url = String.format(appConfig.getQqUrlAuthorization(), appConfig.getQqAppId(), URLEncoder.encode(appConfig.getQqUrlRedirect(), StandardCharsets.UTF_8), state);
        return getSuccessResponseVO(url);
    }

    @PostMapping("/qqlogin/callback")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO qqloginCallback(HttpSession session, @VerifyParam(required = true) String code, @VerifyParam(required = true) String state) {
        SessionWebUserDto sessionWebUserDto = userInfoService.qqlogin(code);

        session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
        Map<String, Object> result = new HashMap<>();

        result.put("callbackUrl", session.getAttribute(state));
        result.put("userInfo", sessionWebUserDto);
        return getSuccessResponseVO(result);
    }
}
