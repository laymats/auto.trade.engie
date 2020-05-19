package com.laymat.core.db.dto;

import com.laymat.core.db.entity.TradeTransaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SaveTradeTransaction extends TradeTransaction {
    private String buyerTradeId;
    private String sellerTradeId;
}
