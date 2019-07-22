package com.laymat.core.engie;

import com.laymat.core.engie.service.TradeMarketStatus;
import com.laymat.core.engie.trade.TradeEngie;
import com.laymat.core.engie.trade.TradeEngieService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        TradeEngieService.getService().addStatusEvent(new TradeMarketStatus());
        TradeEngieService.getService().startEngie();
    }
}
