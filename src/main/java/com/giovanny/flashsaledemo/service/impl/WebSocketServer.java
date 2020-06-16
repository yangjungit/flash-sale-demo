package com.giovanny.flashsaledemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

//import org.apache.commons.lang3.StringUtils;

/**
 * @packageName: com.giovanny.mybatisplusmq.common.utils
 * @className: WebSocketServer
 * @description:
 * @author: YangJun
 * @date: 2020/5/15 10:17
 * @version: v1.0
 **/
@ServerEndpoint("/ws/server/{userId}")
@Component
@Slf4j
public class WebSocketServer {


    /*
     * 1、因为WebSocket是类似客户端服务端的形式(采用ws协议)，那么这里的WebSocketServer其实就相当于一个ws协议的Controller
     * 2、直接@ServerEndpoint("/imserver/{userId}") 、@Component启用即可，
     * 然后在里面实现@OnOpen开启连接，@onClose关闭连接，@onMessage接收消息等方法。
     * 3、新建一个ConcurrentHashMap webSocketMap 用于接收当前userId的WebSocket，方便IM之间对userId进行推送消息。单机版实现到这里就可以。
     * 4、集群版（多个ws节点）还需要借助mysql或者redis等进行处理，改造对应的sendMessage方法即可。
     *
     * */


    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的(可以加volatile或者使用synchronized对操作加锁)。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());
        log.info("所有用户:{}", webSocketMap.keySet());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSONObject.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getString("toUserId");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                } else {
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}

