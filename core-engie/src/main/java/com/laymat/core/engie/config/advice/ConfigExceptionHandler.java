package com.laymat.core.engie.config.advice;

import com.laymat.core.db.utils.exception.BaseRestfulException;
import com.laymat.core.db.utils.exception.impl.UnboundMobileException;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

/**
 * 定义未处理异常统一拦截
 */
@ControllerAdvice
public class ConfigExceptionHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e instanceof UnboundMobileException) {
            return SimpleResult.NEED_BOUND_MOBILE;
        }
        if (e instanceof BaseRestfulException) {
            if (e.getCause() != null) {
                return SimpleResult.retSystemMessageFail(e.getCause().getMessage());
            } else {
                return SimpleResult.retSystemMessageFail(e.getMessage());
            }
        }

        logger.error("系统异常：{}", e);

        if (e instanceof HttpMessageNotReadableException) {
            return SimpleResult.retSystemMessageFail("缺少数据请求对象，请检查body内容");
        }

        if (e instanceof BadSqlGrammarException) {
            return SimpleResult.retSystemMessageFail("数据库执行异常，请稍后再试");
        }

        return SimpleResult.retSystemMessageFail(e.getMessage());
    }

    /**
     * 验证异常
     *
     * @param req
     * @param e
     * @return
     * @throws MethodArgumentNotValidException
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseRestfulResult handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        var stringJoiner = new StringJoiner("|");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringJoiner.add(fieldError.getDefaultMessage() + fieldError.getArguments());
        }

        logger.error("参数验证异常", e);
        return SimpleResult.retMessageFail(stringJoiner.toString());
    }
}