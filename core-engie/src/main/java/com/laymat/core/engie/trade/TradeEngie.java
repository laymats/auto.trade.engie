package com.laymat.core.engie.trade;

import com.laymat.core.engie.trade.order.TradeOrder;
import com.laymat.core.engie.trade.order.TradeResult;
import com.laymat.core.engie.trade.subscribe.TradeMarketSubscribe;
import com.laymat.core.engie.trade.subscribe.TradeStatus;
import jdk.jfr.Event;

import java.util.List;

public interface TradeEngie {
    boolean running();
    boolean startEngie();
    boolean stopEngie();
    boolean placeOrder(TradeOrder order);
    boolean cancelOrder(TradeOrder order);
    List<TradeOrder>  getBuyers();
    List<TradeOrder>  getSellers();
    TradeResult[] getTradeResults();
    TradeStatus getTradeStatus();
    void addStatusEvent(TradeMarketSubscribe subscribe);
}
