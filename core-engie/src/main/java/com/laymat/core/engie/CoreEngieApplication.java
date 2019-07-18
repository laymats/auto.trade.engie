package com.laymat.core.engie;

import com.laymat.core.engie.trade.TradeEngieService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreEngieApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreEngieApplication.class, args);
        TradeEngieService.getService().startEngie();
    }

}
