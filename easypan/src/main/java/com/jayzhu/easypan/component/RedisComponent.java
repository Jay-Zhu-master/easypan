package com.jayzhu.easypan.component;

import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.DownloadFileDto;
import com.jayzhu.easypan.entity.dto.SysSettingDto;
import com.jayzhu.easypan.entity.dto.UserSpaceDto;
import com.jayzhu.easypan.mapper.FileInfoMapper;
import com.jayzhu.easypan.utils.RedisUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisComponent {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    /**
     * 从redis获取系统设置
     *
     * @return 封装的系统设置信息对象
     */
    public SysSettingDto getSysSettingDto() {
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingDto == null) {
            sysSettingDto = new SysSettingDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingDto);
        }
        return sysSettingDto;
    }

    /**
     * redis存储用户使用空间信息
     *
     * @param userId       用户id
     * @param userSpaceDto 封装的用户空间信息对象
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * 获取用户使用空间信息
     *
     * @param userId 用户id
     * @return 封装的使用空间信息对象
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (userSpaceDto == null) {
            userSpaceDto = new UserSpaceDto();
            userSpaceDto.setUseSpace(fileInfoMapper.selectUseSpace(userId));
            userSpaceDto.setTotalSpace(getSysSettingDto().getUserInitUserSpace() * Constants.MB);
            saveUserSpaceUse(userId, userSpaceDto);
        }
        return userSpaceDto;
    }

    /**
     * 获取临时文件大小
     *
     * @param userId 用户id
     * @param fileId 文件id
     * @return long类型文件大小
     */
    public Long getFileTempSize(String userId, String fileId) {
        return getFileSizeFormRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
    }

    /**
     * redis存储临时文件大小信息
     *
     * @param userId   用户id
     * @param fileId   文件id
     * @param fileSize 文件大小
     */
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize, Constants.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    public Long getFileSizeFormRedis(String key) {
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return Long.valueOf((Integer) sizeObj);
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }
        return 0L;
    }

    /**
     * 向redis中存储文件下载信息
     *
     * @param downloadFileDto 封装的文件下载信息
     */
    public void saveDownloadCode(DownloadFileDto downloadFileDto) {
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD + downloadFileDto.getDownloadCode(), downloadFileDto, Constants.REDIS_KEY_EXPIRES_ONE_MIN);
    }

    /**
     * 从redis获取文件下载信息
     *
     * @param code redis key校验码
     * @return 文件下载封装信息
     */
    public DownloadFileDto getDownloadCode(String code) {
        return ((DownloadFileDto) redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code));
    }
}
