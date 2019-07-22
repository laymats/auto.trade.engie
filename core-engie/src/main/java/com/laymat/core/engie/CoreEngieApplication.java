package com.laymat.core.engie;

import com.laymat.core.engie.service.TradeMarketStatus;
import com.laymat.core.engie.trade.TradeEngieService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

@Configuration
@SpringBootApplication
@ComponentScan(basePackages = {"com.laymat"})
@MapperScan({"com.laymat.core.db.dao"})
public class CoreEngieApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreEngieApplication.class, args);

        TradeEngieService.getService().addStatusEvent(new TradeMarketStatus());
        TradeEngieService.getService().startEngie();
    }
}
