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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MySocketHandler extends TextWebSocketHandler {
    private Map<Integer, WebSocketSession> mp = new ConcurrentHashMap<Integer, WebSocketSession>();
    private Map<Integer, Boolean> roomStatusMp = new ConcurrentHashMap<Integer, Boolean>();
    // true -> opened / false -> closed
    private Map<WebSocketSession, Integer> invmp = new ConcurrentHashMap<WebSocketSession, Integer>();
    final Logger logger = LoggerFactory.getLogger(MySocketHandler.class);
    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("null")
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        String dat = (String) message.getPayload();
        Map<String, Object> pack = objectMapper.readValue(dat, new TypeReference<Map<String, Object>>() {
        });

        Integer toId = (Integer) pack.get("toId");
        if (toId != null && mp.containsKey(toId)) {
            mp.get(toId).sendMessage(message);
        } else {
            // logger.warn("Invalid recipient: {}", toId);
        }
    }

    @Override
    @SuppressWarnings("null")
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // logger.error("Exception occured : {}", exception);
    }

    @Override
    @SuppressWarnings("null")
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = mp.size();
        mp.put(userId, session);
        invmp.put(session, userId);
        roomStatusMp.put(userId, false);
        Integer clientList[] = mp.keySet().toArray(new Integer[0]);
        Boolean clientStatusList[] = new Boolean[clientList.length];
        for (int i = 0; i < clientList.length; i++) {
            clientStatusList[i] = roomStatusMp.get(clientList[i]);
        }

        // logger.info("connect with : " + userId);

        // send to myself
        MyWebSocketMessage initMessage = new MyWebSocketMessage(
                MessageType.INITIAL,
                "---",
                userId,
                clientList, clientStatusList);
        String json = objectMapper.writeValueAsString(initMessage);
        TextMessage textMessage = new TextMessage(json);
        session.sendMessage(textMessage);

        // broadcast to everyone
        MyWebSocketMessage broadCastMessage = new MyWebSocketMessage(
                MessageType.BROADCAST,
                "user",
                -1,
                clientList,
                clientStatusList);
        json = objectMapper.writeValueAsString(broadCastMessage);
        textMessage = new TextMessage(json);
        for (WebSocketSession ws : invmp.keySet()) {
            ws.sendMessage(textMessage);
        }
    }

    @Override
    @SuppressWarnings("null")
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userId = invmp.remove(session);
        mp.remove(userId);
        roomStatusMp.remove(userId);
        broadcastToEveryone();
        // Integer clientList[] = mp.keySet().toArray(new Integer[0]);
        // Boolean clientStatusList[] = new Boolean[clientList.length];
        // for (int i = 0; i < clientList.length; i++) {
        // clientStatusList[i] = roomStatusMp.get(clientList[i]);
        // }
        // MyWebSocketMessage broadCastMessage = new MyWebSocketMessage(
        // MessageType.BROADCAST,
        // "---",
        // -1,
        // clientList,
        // clientStatusList);
        // String json = objectMapper.writeValueAsString(broadCastMessage);
        // TextMessage textMessage = new TextMessage(json);
        // for (WebSocketSession ws : invmp.keySet()) {
        // ws.sendMessage(textMessage);
        // }
        // logger.info("leave : {} | status : {}", session.getId(), status.getCode());
    }

    public Boolean openRoom(int id) throws JsonProcessingException, IOException {
        Boolean ret = false;
        if (!roomStatusMp.get(id))
            ret = true; // newly open
        roomStatusMp.put(id, true);
        broadcastToEveryone();
        return ret;
    }

    public Boolean closeRoom(int userId) throws JsonProcessingException, IOException {
        Boolean ret = false;
        roomStatusMp.get(userId);
        if (roomStatusMp.get(userId))
            ret = true; // newly close
        roomStatusMp.put(userId, false);
        broadcastToEveryone();
        return ret;
    }

    private void broadcastToEveryone() throws JsonProcessingException, IOException {
        Integer clientList[] = mp.keySet().toArray(new Integer[0]);
        Boolean clientStatusList[] = new Boolean[clientList.length];
        for (int i = 0; i < clientList.length; i++) {
            clientStatusList[i] = roomStatusMp.get(clientList[i]);
        }
        MyWebSocketMessage broadCastMessage = new MyWebSocketMessage(
                MessageType.BROADCAST,
                "user",
                -1,
                clientList,
                clientStatusList);
        String json = objectMapper.writeValueAsString(broadCastMessage);
        TextMessage textMessage = new TextMessage(json);
        for (WebSocketSession ws : invmp.keySet()) {
            ws.sendMessage(textMessage);
        }
    }
}
