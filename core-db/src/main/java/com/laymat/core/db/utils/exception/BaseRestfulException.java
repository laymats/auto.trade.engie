package com.laymat.core.db.utils.exception;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
public class BaseRestfulException extends RuntimeException {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected Gson gson = new Gson();

    public BaseRestfulException(Exception ex) {
        super(ex);
    }

    public BaseRestfulException(String message, Object... arg) {
        super(String.format(message, arg));
        logger.error(gson.toJson(this.getStackTrace()));
    }
}