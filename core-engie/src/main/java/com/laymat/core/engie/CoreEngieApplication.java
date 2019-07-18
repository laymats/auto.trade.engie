package com.laymat.core.engie;

import com.laymat.core.engie.service.TradeMarketStatus;
import com.laymat.core.engie.trade.TradeEngieService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;

@Configuration
@SpringBootApplication
public class CoreEngieApplication implements ApplicationListener {

    public static void main(String[] args) {
        SpringApplication.run(CoreEngieApplication.class, args);

        TradeEngieService.getService().addStatusEvent(new TradeMarketStatus());
        TradeEngieService.getService().startEngie();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextStoppedEvent) {
            TradeEngieService.getService().stopEngie();
        }
    }
}
