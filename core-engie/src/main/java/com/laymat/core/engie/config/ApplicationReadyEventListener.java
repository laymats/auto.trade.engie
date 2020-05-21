package com.laymat.core.engie.config;

import com.laymat.core.engie.robot.RobotService;
import com.laymat.core.engie.service.TradeMarketStatus;
import com.laymat.core.engie.trade.TradeEngie;
import com.laymat.core.engie.trade.TradeEngieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    protected TradeEngieService tradeEngieService;
    @Autowired
    protected RobotService  robotService;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        tradeEngieService.addStatusEvent(new TradeMarketStatus());
        tradeEngieService.startEngie();

        //robotService.start();
    }
}
