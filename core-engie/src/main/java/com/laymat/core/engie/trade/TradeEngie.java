package com.laymat.core.engie.trade;

import com.laymat.core.engie.trade.order.TradeOrder;
import com.laymat.core.engie.trade.order.TradeResult;

import java.util.List;

public interface TradeEngie {
    boolean startEngie();
    boolean stopEngie();
    boolean placeOrder(TradeOrder order);
    boolean cancelOrder(TradeOrder order);
    List<TradeOrder>  getBuyers();
    List<TradeOrder>  getSellers();
    TradeResult[] getTradeResults();
    TradeStatus getTradeStatus();
}
