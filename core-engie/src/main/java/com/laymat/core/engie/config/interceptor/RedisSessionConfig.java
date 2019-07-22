package com.laymat.core.engie.config.interceptor;


import cn.hutool.core.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 24)
public class RedisSessionConfig {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private HttpServletRequest request;

    private static final String requestSessionKey = "trade-session-key";

    public RedisSessionConfig(HttpServletRequest request){
        this.request = request;
    }
    public boolean checkUserSession(){
        return request.isRequestedSessionIdValid();
    }

    public Object getUserSession(){
        return request.getSession().getAttribute(requestSessionKey);
    }

    public String saveSession(Object object) {
        request.getSession().setAttribute(requestSessionKey, object);
        var sessionId = request.getSession().getId();
        return Base64.encode(sessionId, "utf-8");
    }

    public void clearSession(HttpServletRequest request) {
        request.getSession().invalidate();
    }
}