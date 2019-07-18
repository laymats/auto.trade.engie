package com.laymat.core.engie.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author dell
 */
@ServerEndpoint("/trade/market/{token}")
@Component
public class TradeMarketService {
    static Log log = LogFactory.get(TradeMarketService.class);
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

        log.info("有新窗口开始监听:{}，当前在线人数为", session, getOnlineCount());
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
        coreTradeMarketServices.remove(this);
        subOnlineCount();           //在线数减1
        log.info("连接关闭，当前在线人数为：{}", getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("接收消息{}的信息:{}", token, message);
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
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendAllSession(String message, @PathParam("token") String token) {
        log.debug("推送对象:{}，推送内容:{}", token, message);
        for (TradeMarketService item : coreTradeMarketServices) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if (token == null) {
                    item.sendMessage(message);
                } else if (item.token.equals(token)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
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


