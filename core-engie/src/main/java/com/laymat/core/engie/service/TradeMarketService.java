package com.laymat.core.engie.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.laymat.core.db.service.UserService;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.engie.trade.subscribe.TradeStatus;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author dell
 */
@ServerEndpoint("/trade/market/{token}")
@Component
public class TradeMarketService {
    static Log log = LogFactory.get(TradeMarketService.class);

    public static UserService userService;
    public static UserTradeOrderService userTradeOrderService;

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(999);
    /**
     * 在线人数
     */
    private volatile static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<TradeMarketService> coreTradeMarketServices = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 当前会话用户相关信息
     */
    private String token = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        this.session = session;
        this.token = token;

        //加入在线集合
        coreTradeMarketServices.add(this);
        addOnlineCount();

        log.info("有新窗口开始监听:{}，当前在线人数为{}", session.getId(), getOnlineCount());
        try {
            sendMessage("{\"status\":\"success\"}");
        } catch (IOException e) {
            log.error("websocket IO异常:" + e.getMessage());
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            coreTradeMarketServices.remove(this);
            subOnlineCount();           //在线数减1
            log.info("连接关闭，当前在线人数为：{}", getOnlineCount());
        } catch (Exception e) {
            log.error("websocket 异常:" + e.getMessage());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            if (message.split("-").length > 0) {
                this.token = message.split("-")[1];
                switch (message.split("-")[0]) {
                    case "account":
                        this.startAccountPush(session, Integer.parseInt(token));
                        break;
                }
            }

            log.info("接收消息{}的信息:{}", token, message);
        } catch (Exception e) {
            log.error("websocket 异常:" + e.getMessage());
        }
    }

    void startAccountPush(Session session, Integer tokens) {
        scheduledExecutorService.execute(() -> {
            while (true) {
                var account = userService.getUserInfo(tokens);
                var orders = userTradeOrderService.getUserTradeOrders(tokens);
                try {
                    if (session.isOpen()) {
                        var map = new HashMap<>();
                        map.put("type", "account");
                        map.put("data", account);
                        map.put("orders", orders);
                        sendMessage(gson.toJson(map));
                        TimeUnit.MILLISECONDS.sleep(500);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    log.error("推送异常：{}", e.getMessage());
                    break;
                }
            }
        });
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误:{}", error.getMessage());
        //error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        try {
            synchronized (this) {
                this.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("websocket 异常:" + e.getMessage());
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendAllSession(TradeStatus tradeStatus) {
        for (TradeMarketService item : coreTradeMarketServices) {
            try {
                if (item.session.isOpen()) {
                    var map = new HashMap<>();
                    map.put("type", "trade");
                    map.put("data", tradeStatus);
                    item.sendMessage(gson.toJson(map));
                    log.debug("推送对象:{}，推送内容:{}", item.session.getId(), gson.toJson(map));
                } else {
                    coreTradeMarketServices.remove(item);
                    subOnlineCount();
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static synchronized void subOnlineCount() {
        onlineCount.decrementAndGet();
    }
}


