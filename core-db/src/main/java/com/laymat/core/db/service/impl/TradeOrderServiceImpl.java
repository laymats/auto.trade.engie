package com.laymat.core.db.service.impl;

import com.laymat.core.db.entity.TradeOrder;
import com.laymat.core.db.dao.TradeOrderDao;
import com.laymat.core.db.service.TradeOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * (TmTradeorder)表服务实现类
 *
 * @author laymat
 * @since 2019-07-19 16:00:23
 */
@Service("tradeOrderService")
public class TradeOrderServiceImpl implements TradeOrderService {
    @Autowired
    private TradeOrderDao tradeOrderDao;

}