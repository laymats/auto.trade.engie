package com.laymat.core.engie.trade.subscribe;

import java.util.EventObject;

public interface TradeMarketSubscribe {
    void doData(TradeStatus tradeStatus);
}
