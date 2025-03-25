package com.calling.websk.dtos;

import java.util.Set;

import com.calling.websk.dtos.enums.MessageType;

public class MyWebSocketMessage {
    private MessageType messageType;
    private String userNickName;
    private Integer userId;
    private Set<Integer> clientList;

    public MessageType getMessageType() {
        return messageType;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public Integer getUserId() {
        return userId;
    }

    public Set<Integer> getClientList() {
        return clientList;
    }

    public MyWebSocketMessage(MessageType messageType, String userNickName, Integer userId, Set<Integer> clientList) {
        this.messageType = messageType;
        this.userNickName = userNickName;
        this.userId = userId;
        this.clientList = clientList;
    }
}
