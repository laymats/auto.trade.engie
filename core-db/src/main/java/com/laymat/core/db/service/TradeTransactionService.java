package com.laymat.core.db.service;

import com.laymat.core.db.dto.SaveTradeTransaction;
import com.laymat.core.db.entity.TradeTransaction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * (TmTradetransaction)表服务接口
 *
 * @author laymat
 * @since 2019-07-19 16:00:23
 */
public interface TradeTransactionService {
    boolean saveTradeTransaction(SaveTradeTransaction tradeTransaction);
}