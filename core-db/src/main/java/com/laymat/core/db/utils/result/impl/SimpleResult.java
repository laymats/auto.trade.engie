package com.laymat.core.db.utils.result.impl;


import com.laymat.core.db.utils.result.BaseRestfulResult;

/**
 * 统一数据返回
 *
 * @param <T> custom type
 * @author laymat
 */
public class SimpleResult<T> extends BaseRestfulResult {
    public SimpleResult(int code, String message, T data) {
        super(code, message, data);
    }

    /**
     * 10000-19999
     * *0100- *0199  用户错误
     * *0200- *0299  订单错误
     * *0300- *0399  其他错误
     */

    /**
     * 系统异常提示
     */
    public static final BaseRestfulResult SYSTEM_FAIL = new SimpleResult(10001, "处理失败", null);
    /**
     * 提示授权失效
     */
    public static final BaseRestfulResult AUTHOR_FAIL = new SimpleResult(11000, "授权失效，请重新授权", null);
    public static final BaseRestfulResult AGENT_AUTHOR_FAIL = new SimpleResult(11000, "经纪人授权失效，请重新授权", null);
    /**
     * 提示绑定手机号码
     */
    public static final BaseRestfulResult NEED_BOUND_MOBILE = new SimpleResult(11001, "请先绑定手机号码", null);
    /**
     * 返回处理成功信息
     */
    public static final BaseRestfulResult SUCCESS = new SimpleResult(0, "处理成功", true);

    /**
     * 返回处理失败数据包
     *
     * @param message 异常信息
     * @return
     */
    public static BaseRestfulResult retMessageFail(String message) {
        return new SimpleResult(12001, message, null);
    }

    /**
     * 系统异常返回
     *
     * @param message
     * @return
     */
    public static BaseRestfulResult retSystemMessageFail(String message) {
        return new SimpleResult(13001, String.format("系统异常：%s", message), null);
    }


    /**
     * 返回处理失败信息，并支持string.format参数格式
     *
     * @param message 异常信息
     * @param arg     异常参数
     * @return
     */
    public static BaseRestfulResult retMessageFail(String message, Object... arg) {
        return new SimpleResult(12001, String.format(message, arg), null);
    }


    /**
     * 返回处理成功信息，并支持string.format参数格式
     *
     * @param message 成功信息
     * @param data    返回书
     * @return
     */
    public static BaseRestfulResult retMessageSuccess(String message, Object data) {
        return new SimpleResult(0, message, data);
    }

    /**
     * 返回处理成功信息
     *
     * @param data 返回数据
     * @return
     */
    public static final BaseRestfulResult retMessageSuccess(Object data) {
        return new SimpleResult(0, "请求成功", data);
    }

    /**
     * 根据传入的data包装返回数据，当data不为null时返回成功，否制返回失败
     *
     * @param data 返回数据
     * @return
     */
    public static BaseRestfulResult retMessageFromData(Object data) {
        if (data == null) {
            return retMessageFail("处理数据失败，请检查请求参数");
        } else {
            return retMessageSuccess("处理成功", data);
        }
    }

    /**
     * 判断boolean结果返回指定信息，当data为true时返回成功，否则返回失败
     *
     * @param data
     * @return
     */
    public static final BaseRestfulResult retMessageFromBoolean(Boolean data) {
        return data ? SUCCESS : SYSTEM_FAIL;
    }
}
