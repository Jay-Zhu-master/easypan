package com.jayzhu.easypan.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO {
    private Integer code;//响应码，1 代表成功; 0 代表失败
    private String msg;  //响应信息 描述字符串
    private String info;
    private Object data; //返回的数据

    //增删改 成功响应
    public static ResponseVO success(){
        return new ResponseVO(1,"success","",null);
    }
    //查询 成功响应
    public static ResponseVO success(Object data){
        return new ResponseVO(1,"success","",data);
    }
    //失败响应
    public static ResponseVO error(String msg){
        return new ResponseVO(0,msg,"失败",null);
    }
}
