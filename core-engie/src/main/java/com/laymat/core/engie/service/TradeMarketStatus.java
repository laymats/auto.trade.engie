package com.laymat.core.engie.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.laymat.core.engie.trade.subscribe.TradeMarketSubscribe;
import com.laymat.core.engie.trade.subscribe.TradeStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class TradeMarketStatus implements TradeMarketSubscribe {

    @Override
    public void doData(TradeStatus tradeStatus) {
        TradeMarketService.sendAllSession(tradeStatus);
    }
}
