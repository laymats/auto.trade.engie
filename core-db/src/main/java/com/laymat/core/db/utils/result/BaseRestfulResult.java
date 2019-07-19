package com.laymat.core.db.utils.result;

import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;

/**
 * 定义统一返回JSON格式
 *
 * @author laymat
 */
@Data
@ApiIgnore
public abstract class BaseRestfulResult<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;
    private Boolean success;


    /**
     * 结果包装对象
     *
     * @param code 状态编码
     * @param msg  状态描述
     * @param data 对象结果
     */
    public BaseRestfulResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = code == 0;
    }

    /**
     * 结果包装对象
     *
     * @param code 状态编码
     * @param msg  状态描述
     */
    public BaseRestfulResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.success = code == 0;
    }

    public BaseRestfulResult() {
    }

    public String toJSON() {
        return new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                //设置 Long 类型自动转换成 String 类型
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create()
                .toJson(this);
    }
}
