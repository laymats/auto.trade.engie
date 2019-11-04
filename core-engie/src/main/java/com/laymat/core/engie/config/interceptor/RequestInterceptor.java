package com.laymat.core.engie.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private List<String> notCheckUrl = new ArrayList<>();

    @PostConstruct
    public void initNotCheckUrl() {
        notCheckUrl.add("/swagger-resources");
        notCheckUrl.add("/webjars");
        notCheckUrl.add("/swagger-ui.html");
        notCheckUrl.add("/userTradeOrder");
    }

    public boolean checkUrl(String url) {
        for (var checkUrl : notCheckUrl) {
            if (url.indexOf(checkUrl) > -1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var url = getRequestFullUrl(request);
        if (logger.isDebugEnabled() && checkUrl(url)) {
            logger.debug("请求Url：{}", url);
        }
        return true;
    }

    private String getRequestFullUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        if (request.getQueryString() != null) {
            url.append('?');
            url.append(request.getQueryString());
        }
        return url.toString();
    }
}
