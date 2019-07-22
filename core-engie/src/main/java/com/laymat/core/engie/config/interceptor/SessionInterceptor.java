package com.laymat.core.engie.config.interceptor;

import com.laymat.core.db.utils.result.impl.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author laymat
 * @date 2019/03/13
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //小程序会话状态校验
        if (new RedisSessionConfig(request).checkUserSession()) {
            return true;
        }

        var ajaxRequest = request.getHeader("content-type") != null
                && request.getHeader("content-type").equals("application/json");

        //判断是否为ajax请求
        if (ajaxRequest) {
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(SimpleResult.retMessageFail("请登录").toJSON());
            response.getWriter().close();
        } else {
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().write("未经授权的访问（401）");
            response.getWriter().close();
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
