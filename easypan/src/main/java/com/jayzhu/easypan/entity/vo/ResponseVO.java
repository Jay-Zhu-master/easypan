package com.jayzhu.easypan.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;
//
//
//    //增删改 成功响应
//    public static ResponseVO success() {
//        return new ResponseVO(200, "success", "请求成功", null);
//    }
//
//    //查询 成功响应
//    public static ResponseVO success(Object data) {
//        return new ResponseVO(200, "success", "请求成功", data);
//    }
//
//    //失败响应
//    public static ResponseVO error(Integer code, String msg) {
//        return new ResponseVO(code, msg, "失败", null);
//    }
}
