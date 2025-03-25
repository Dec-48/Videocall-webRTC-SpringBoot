package com.calling.websk.components;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.calling.websk.dtos.MyWebSocketMessage;
import com.calling.websk.dtos.enums.MessageType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MySocketHandler extends TextWebSocketHandler {
    private Map<Integer, WebSocketSession> mp = new ConcurrentHashMap<Integer, WebSocketSession>();
    private Map<WebSocketSession, Integer> invmp = new ConcurrentHashMap<WebSocketSession, Integer>();
    final Logger logger = LoggerFactory.getLogger(MySocketHandler.class);
    final ObjectMapper objectMapper = new ObjectMapper();

    public Map<Integer, WebSocketSession> getMap() {
        return mp;
    }

    @Override
    @SuppressWarnings("null")
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        String dat = (String) message.getPayload();
        logger.info(dat);
        Map<String, Object> pack = objectMapper.readValue(dat, new TypeReference<Map<String, Object>>() {
        });
        Integer toId = (Integer) pack.get("toId");
        if (toId != null && mp.containsKey(toId)) {
            mp.get(toId).sendMessage(message);
        } else {
            logger.warn("Invalid recipient: {}", toId);
        }
    }

    @Override
    @SuppressWarnings("null")
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Exception occured : {}", exception);
    }

    @Override
    @SuppressWarnings("null")
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = mp.size();
        logger.info("connect with : " + userId);
        mp.put(userId, session);
        invmp.put(session, userId);

        // send to myself
        MyWebSocketMessage initMessage = new MyWebSocketMessage(
                MessageType.INITIAL,
                "---",
                userId,
                mp.keySet());
        String json = objectMapper.writeValueAsString(initMessage);
        TextMessage textMessage = new TextMessage(json);
        session.sendMessage(textMessage);

        // broadcast to everyone
        MyWebSocketMessage broadCastMessage = new MyWebSocketMessage(
                MessageType.BROADCAST,
                "user",
                -1,
                mp.keySet());
        json = objectMapper.writeValueAsString(broadCastMessage);
        textMessage = new TextMessage(json);
        for (WebSocketSession ws : invmp.keySet()) {
            if (ws.getId().equals(session.getId()))
                continue;
            ws.sendMessage(textMessage);
        }
    }

    @Override
    @SuppressWarnings("null")
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer id = invmp.remove(session);
        mp.remove(id);
        MyWebSocketMessage broadCastMessage = new MyWebSocketMessage(
                MessageType.BROADCAST,
                "---",
                -1,
                mp.keySet());
        String json = objectMapper.writeValueAsString(broadCastMessage);
        TextMessage textMessage = new TextMessage(json);
        for (WebSocketSession ws : invmp.keySet()) {
            ws.sendMessage(textMessage);
        }
        logger.info("leave : {} | status : {}", session.getId(), status.getCode());
    }

    // @Override
    // public boolean supportsPartialMessages() {
    // return false;
    // }

}
