package com.laymat.core.db.utils.exception.impl;


import com.laymat.core.db.utils.exception.BaseRestfulException;

/**
 * 未绑定手机异常
 */
public class UnboundMobileException extends BaseRestfulException {
    public UnboundMobileException(String message, Object... arg) {
        super(message, arg);
    }
}
