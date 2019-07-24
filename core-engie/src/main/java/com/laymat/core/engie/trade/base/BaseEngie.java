package com.laymat.core.engie.trade.base;

import com.laymat.core.db.service.TradeTransactionService;
import com.laymat.core.db.service.UserTradeOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseEngie {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 数据库交互相关
     */
    @Autowired
    protected TradeTransactionService tradeTransactionService;

    @Autowired
    protected UserTradeOrderService userTradeOrderService;
}
