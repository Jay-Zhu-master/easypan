package com.jayzhu.easypan.component;

import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SysSettingDto;
import com.jayzhu.easypan.entity.dto.UserSpaceDto;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisComponent {
    @Autowired
    private RedisUtils redisUtils;

    public SysSettingDto getSysSettingDto() {
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingDto == null) {
            sysSettingDto = new SysSettingDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingDto);
        }
        return sysSettingDto;
    }

    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (userSpaceDto == null) {
            userSpaceDto = new UserSpaceDto();
            userSpaceDto.setUseSpace(0L);
            userSpaceDto.setTotalSpace(getSysSettingDto().getUserInitUserSpace()*Constants.MB);
            saveUserSpaceUse(userId,userSpaceDto);
        }
        return userSpaceDto;
    }
}
