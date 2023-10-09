package com.jayzhu.easypan.entity.enums;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月07日 17:08:20
 * @packageName com.jayzhu.easypan.enums
 * @className ResopnseCodeEnum
 */
public enum ResponseCodeEnum {
    CODE_200(200, "请求成功"),
    CODE_404(404, "请求地址不存在"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在"),
    CODE_500(500, "服务器返回错误，请联系管理员"),
    CODE_901(901, "登陆超时，请重新登陆"),
    CODE_904(904, "网盘空间不足");
    private Integer code;
    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
