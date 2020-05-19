package com.laymat.core.engie.config;

import com.laymat.core.db.service.UserService;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.engie.config.interceptor.RequestInterceptor;
import com.laymat.core.engie.config.interceptor.SessionInterceptor;
import com.laymat.core.engie.service.TradeMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author laymat
 * @date 2019/03/13
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    @Autowired
    UserService userService;
    @Autowired
    UserTradeOrderService userTradeOrderService;

    /**
     * 注册 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var excludePaths = new String[]{
                "/static/**",
                "/index.html",
                "/user/login",
                //actuator
                "/actuator/**",
                //swagger exclude
                "/webjars/**",
                "/v2/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
        };
        // 设置拦截的路径、不拦截的路径、优先级等等
        registry.addInterceptor(getSessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludePaths);
        //拦截所有请求
        registry.addInterceptor(getRequestInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public HandlerInterceptor getSessionInterceptor() {
        return new SessionInterceptor();
    }

    @Bean
    public HandlerInterceptor getRequestInterceptor() {
        return new RequestInterceptor();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    }

    private CorsConfiguration addcorsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> list = new ArrayList<>();
        list.add("*");

        corsConfiguration.setAllowedOrigins(list);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", addcorsConfig());
        return new CorsFilter(source);
    }

    @Bean
    public void init() {
        TradeMarketService.userService = this.userService;
        TradeMarketService.userTradeOrderService = this.userTradeOrderService;
    }
}
