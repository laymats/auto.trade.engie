package com.laymat.core.db.dto;

import com.laymat.core.db.entity.TradeTransaction;
import lombok.Data;

@Data
public class SaveTradeTransaction extends TradeTransaction {
    private String buyerTradeId;
    private String sellerTradeId;
}
