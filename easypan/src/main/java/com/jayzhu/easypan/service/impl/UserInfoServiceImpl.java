package com.jayzhu.easypan.service.impl;

import com.jayzhu.easypan.component.RedisComponent;
import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.dto.SysSettingDto;
import com.jayzhu.easypan.entity.dto.UserSpaceDto;
import com.jayzhu.easypan.entity.po.UserInfo;
import com.jayzhu.easypan.entity.enums.UserStatusEnum;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.mapper.FileInfoMapper;
import com.jayzhu.easypan.mapper.UserInfoMapper;
import com.jayzhu.easypan.service.EmailCodeService;
import com.jayzhu.easypan.service.UserInfoService;
import com.jayzhu.easypan.utils.StringTools;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    AppConfig appConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password, String emailCode) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱账号已存在");
        }
        UserInfo nickNameUser = userInfoMapper.selectByNickName(nickName);
        if (nickNameUser != null) {
            throw new BusinessException("昵称已存在");
        }
        // 校验邮箱验证码
        emailCodeService.checkCode(email, emailCode);
        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickName);
        userInfo.setEmail(email);
        userInfo.setPassword(StringTools.encodeByMd5(password));
        userInfo.setJoinTime(LocalDateTime.now());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setUseSpace(0L);
        SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();
        userInfo.setTotalSpace(sysSettingDto.getUserInitUserSpace() * Constants.MB);
        userInfoMapper.insert(userInfo);
    }

    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo == null || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账号或密码错误");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        userInfo.setLastLoginTime(LocalDateTime.now());
        userInfoMapper.updateByUserId(userInfo, userInfo.getUserId());
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userInfo.getUserId());
        if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), email)) {
            sessionWebUserDto.setAdmin(true);
        } else {
            sessionWebUserDto.setAdmin(false);
        }
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        userSpaceDto.setUseSpace(fileInfoMapper.selectUseSpace(userInfo.getUserId()));
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
        return sessionWebUserDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("邮箱账号不存在");
        }
        emailCodeService.checkCode(email, emailCode);
        userInfo.setPassword(StringTools.encodeByMd5(password));
        userInfoMapper.updateByUserId(userInfo, userInfo.getUserId());
    }

    @Override
    public SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        return (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
    }

    public void updateUserInfoByUserId(UserInfo userInfo, String userId) {
        userInfoMapper.updateByUserId(userInfo, userId);
    }

    @Override
    public SessionWebUserDto qqlogin(String code) {
        return null;
    }
}
