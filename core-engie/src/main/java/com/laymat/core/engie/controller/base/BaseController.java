package com.laymat.core.engie.controller.base;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laymat.core.db.entity.User;
import com.laymat.core.engie.config.interceptor.RedisSessionConfig;
import com.laymat.core.engie.trade.TradeEngieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * Controller公共组件
 */
public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected TradeEngieService tradeEngieService;

    public User getSession() {
        return (User)new RedisSessionConfig(request).getUserSession();
    }

    /**
     * 获取用户真实请求ip地址
     *
     * @return ip地址
     */
    public String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-FORWARDED-FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_REAL_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.indexOf(",") >= 0) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    protected String getContextPath() {
        return request.getContextPath();
    }

    /**
     * query request param convert to integer
     *
     * @param objName
     * @return
     */
    protected Integer getRequestInt(String objName) {
        return Integer.parseInt(request.getParameter(objName));
    }

    /**
     * query request param convert to String
     *
     * @param objName
     * @return
     */
    protected String getRequestStr(String objName) {
        return request.getParameter(objName);
    }


    /**
     * 获取request指定数组对象
     *
     * @param objName
     * @return
     */
    protected String[] getRequestArray(String objName) {
        return request.getParameter(objName).split(",");
    }


    protected Page getNewPage() {
        var size = getRequestInt("size");
        var page = getRequestInt("page");
        if (size == null || size == 0) {
            size = 20;
        }
        if (page == null || page == 0) {
            page = 1;
        }
        return new Page(page, size);
    }


    protected void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}