package com.jayzhu.easypan.entity.query;

import lombok.Data;

import java.util.Date;


/**
 * 参数
 */
@Data
public class UserInfoQuery extends BaseParam {
    /**
     *
     */
    private Integer id;
    /**
     *
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     *
     */
    private Integer age;

    /**
     *
     */
    private String code;

    private String codeFuzzy;

    /**
     *
     */
    private String email;

    private String emailFuzzy;

    /**
     *
     */
    private Integer status;

    /**
     *
     */
    private String code02;

    private String code02Fuzzy;

    /**
     *
     */
    private String code03;

    private String code03Fuzzy;

    /**
     *
     */
    private Long test;


}
