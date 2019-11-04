package com.laymat.core.engie.config.advice;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Type;


@ControllerAdvice
public class ConfigRequestAdvice implements RequestBodyAdvice {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    HttpServletRequest request;

    protected Gson gson = new Gson();

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object object, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        Method method = methodParameter.getMethod();

        /**
         * 填充请求对象关联当前用户信息
         */
//        if (object instanceof BaseUserSession) {
//            var userSessionDTO = (BaseUserSession) object;
//
//            var session = new RedisSessionConfig(request);
//
//            if (session.checkSession()) {
//                var userSession = session.getUserSession();
//                userSessionDTO.setThirdId(userSession.getThirdId());
//                userSessionDTO.setUserId(userSession.getUserId());
//                userSessionDTO.setOpenId(userSession.getOpenId());
//            }
//            if(session.checkAdminSession()){
//                var adminSession = session.getAdminSession();
//                userSessionDTO.setServiceUserId(adminSession.getServiceUserId());
//            }
//            object = userSessionDTO;
//        }

        /**
         * 输出请求参数
         */
        //logger.info("请求Controller{}.{}:{}", method.getDeclaringClass().getSimpleName(), method.getName(), gson.toJson(object));
        return object;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        Method method = methodParameter.getMethod();
        logger.info("{}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        return o;
    }
}
