package com.laymat.core.engie.robot;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import com.laymat.core.engie.robot.entity.MakeTradeBean;
import com.laymat.core.engie.trade.TradeEngieService;
import com.laymat.core.engie.trade.order.TradeOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: laymat
 * @desc:
 * @date: 2020/5/20
 */
@Component
public class RobotService {
    @Autowired
    protected TradeEngieService tradeEngieService;

    @Autowired
    UserTradeOrderService userTradeOrderService;

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected final String ltc_url = "https://www.okex.me/api/futures/v3/instruments/LTC-USDT-200626/trades";
    protected Gson gson = new Gson();

    public void start() {
        new Thread(() -> {
            logger.info("网格机器人已启动.");
            while (true) {
                try {
                    this.autoTrade();
                } catch (Exception e) {
                    logger.error("Robot 异常1：{}", e.getMessage());
                }
            }
        }).start();
    }

    void autoTrade() throws InterruptedException {
        while (true) {
            var realTimeDatas = HttpUtil.get(ltc_url, 3000);
            var type = new TypeToken<List<MakeTradeBean>>() {
            }.getType();
            var makeTrades = (List<MakeTradeBean>) gson.fromJson(realTimeDatas, type);
            logger.info("获取ltc合约成功.");

            if (tradeEngieService.running()) {
                try {
                    var robotList = new Integer[]{3, 4};
                    var currentRobot = robotList[RandomUtil.randomInt(0, robotList.length)];
                    makeTrades.forEach(x -> {
                        var saveUserOrder = new SaveUserOrder();
                        saveUserOrder.setUserId(currentRobot);
                        saveUserOrder.setTradePrice(x.getPrice());
                        saveUserOrder.setTradeCount(x.getQty().divide(new BigDecimal("2")));
                        saveUserOrder.setBuyer(x.getSide().equalsIgnoreCase("buy") ? 1 : 0);
                        saveUserOrder.setMarketOrder(0);
                        saveUserOrder.setCancel(0);
                        if (userTradeOrderService.placeOrder(saveUserOrder)) {
                            var order = new TradeOrder();
                            order.setTradeId(saveUserOrder.getTradeId());
                            order.setBuyer(saveUserOrder.getBuyer() == 1);
                            order.setMarketOrder(saveUserOrder.getMarketOrder() == 1);
                            order.setCancel(saveUserOrder.getCancel() == 1);
                            order.setTradePrice(saveUserOrder.getTradePrice());
                            order.setTradeCount(saveUserOrder.getTradeCount());
                            order.setUserId(saveUserOrder.getUserId());

                            tradeEngieService.placeOrder(order);
                            logger.debug("Robot下单成功：价格{}，数量{}，方向{}",
                                    order.getTradePrice(),
                                    order.getTradeCount(),
                                    order.isBuyer() ? "买" : "卖");
                        }

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            logger.error("Robot forEach 异常：{}", e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    logger.error("Robot 异常2：{}", e.getMessage());
                }
            }
            TimeUnit.MILLISECONDS.sleep(3000);
        }
    }
}
