package com.calling.websk.compo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MySocketHandler extends TextWebSocketHandler{
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private Map<Integer, WebSocketSession> mp = new HashMap<Integer, WebSocketSession>();
    private Map<WebSocketSession, Integer> invmp = new HashMap<WebSocketSession, Integer>();
    final Logger logger = LoggerFactory.getLogger(MySocketHandler.class);
    final ObjectMapper objectMapper = new ObjectMapper();

    public Map<Integer, WebSocketSession> getMap(){
        return mp;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
        String dat = (String) message.getPayload();
        logger.info(dat);
        Map<String,Object> pack = objectMapper.readValue(dat, HashMap.class);
        Integer toId = (Integer) pack.get("toId");
        mp.get(toId).sendMessage(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Exception occured : {}", exception);        
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("connect : {} :)", session.getId());
        Integer myId = mp.size();
        mp.put(myId, session);
        invmp.put(session, myId);
        
        String json = "{\"type\" : \"initial\", \"myId\" : %d, \"list\" : \"%s\"}";
        json = String.format(json, myId, objectMapper.writeValueAsString(mp.keySet()));
        session.sendMessage(new TextMessage(json));
        json = "{\"type\" : \"fetchList\", \"list\" : \"%s\"}";
        json = String.format(json, objectMapper.writeValueAsString(mp.keySet()));
        for (WebSocketSession ws : invmp.keySet()){
            // if (!ws.getId().equals(session.getId())){
            ws.sendMessage(new TextMessage(json));
            // }
        }
        sessions.add(session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        WebSocketSession deleted = mp.remove(invmp.get(session));
        invmp.remove(session);
        String json = "{\"type\" : \"fetchList\", \"list\" : \"%s\"}";
        json = String.format(json, objectMapper.writeValueAsString(mp.keySet()));
        for (WebSocketSession ws : invmp.keySet()){
            // if (!ws.getId().equals(session.getId())){
            ws.sendMessage(new TextMessage(json));
            // }
        }

        logger.info("leave : {} | status : {}", session.getId(), status.getCode());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
}
