package com.jayzhu.easypan.service.impl;

import com.jayzhu.easypan.component.RedisComponent;
import com.jayzhu.easypan.entity.config.AppConfig;
import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SysSettingDto;
import com.jayzhu.easypan.entity.po.EmailCode;
import com.jayzhu.easypan.entity.po.UserInfo;
import com.jayzhu.easypan.exception.BusinessException;
import com.jayzhu.easypan.mapper.EmailCodeMapper;
import com.jayzhu.easypan.mapper.UserInfoMapper;
import com.jayzhu.easypan.service.EmailCodeService;
import com.jayzhu.easypan.utils.StringTools;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class EmailCodeServiceImpl implements EmailCodeService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private EmailCodeMapper emailCodeMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RedisComponent redisComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailCode(String email, Integer type) throws BusinessException {
        if (Objects.equals(type, Constants.ZERO)) {
            UserInfo userInfo = userInfoMapper.selectByEmail(email);
            if (userInfo != null) {
                throw new BusinessException("邮箱已存在");
            }
        }
        String code = StringTools.getRandomNumber(Constants.LENGTH_5);
        sendEmailCode(email, code);
        emailCodeMapper.disableEmailCode(email);
        EmailCode emailCode = new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(Constants.ZERO);
        emailCode.setCreateTime(LocalDateTime.now());
        emailCodeMapper.insert(emailCode);

    }

    @Override
    public void checkCode(String email, String code) {
        EmailCode emailCode = emailCodeMapper.selectByEmailAndCode(email, code);
        if (emailCode == null) {
            throw new BusinessException("邮箱验证码不正确");
        }
        if (emailCode.getStatus() == 1 || Duration.between(emailCode.getCreateTime(), LocalDateTime.now()).toMinutes() >= Constants.LENGTH_15) {
            throw new BusinessException("邮箱验证码已过期");
        }
        emailCodeMapper.disableEmailCode(email);
    }

    private void sendEmailCode(String toEmail, String code) throws BusinessException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(appConfig.getSendUsername());
            helper.setTo(toEmail);

            SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();

            helper.setSubject(sysSettingDto.getRegisterMailTitle());
            helper.setText(String.format(sysSettingDto.getRegisterEmailContent(), code));

            helper.setSentDate(new Date());
            mailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            throw new BusinessException("邮件发送失败");
        }

    }
}
