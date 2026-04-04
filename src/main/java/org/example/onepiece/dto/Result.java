package org.example.onepiece.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Data
//@NoArgsConstructor

@AllArgsConstructor
//public class Result {
//    private Boolean success;
//    private String errorMsg;
//    private Object data;
//    private Long total;
//
//    public static Result ok(){
//        return new Result(true, null, null, null);
//    }
//    public static Result ok(Object data){
//        return new Result(true, null, data, null);
//    }
//    public static Result ok(List<?> data, Long total){
//        return new Result(true, null, data, total);
//    }
//    public static Result fail(String errorMsg){
//        return new Result(false, errorMsg, null, null);
//    }
//}


/**
 * 黑马点评标准响应类
 */
@Data
public class Result {
    @JsonProperty("code")
    private Integer code; // 业务状态码
    @JsonProperty("msg")
    private String msg;   // 提示信息
    @JsonProperty("data")
    private Object data;  // 业务数据

    // 私有构造器
    private Result() {}

    // 成功：无数据
    public static Result ok() {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("操作成功");
        result.setData(null);
        return result;
    }

    // 成功：有数据
    public static Result ok(Object data) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 成功：自定义提示+数据
    public static Result ok(String msg, Object data) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 失败：自定义状态码+提示
    public static Result fail(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    // 失败：默认400+自定义提示
    public static Result fail(String msg) {
        return fail(400, msg);
    }
}