package com.laymat.core.db.utils.exception.impl;


import com.laymat.core.db.utils.exception.BaseRestfulException;

public class SimpleException extends BaseRestfulException {

    public SimpleException(Exception e){
        super(e);
    }
    public SimpleException(String message, Object... arg) {
        super(message, arg);
    }
}
