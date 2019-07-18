package com.laymat.core.engie.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.laymat.core.engie.trade.subscribe.TradeMarketSubscribe;
import com.laymat.core.engie.trade.subscribe.TradeStatus;

import java.io.IOException;

public class TradeMarketStatus implements TradeMarketSubscribe {
    private Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    @Override
    public void doData(TradeStatus tradeStatus) {
        TradeMarketService.sendAllSession(gson.toJson(tradeStatus), null);
    }
}
