package com.calling.websk.dtos;

import com.calling.websk.dtos.enums.MessageType;

public class MyWebSocketMessage {
    private MessageType messageType;
    private String userNickName;
    private Integer userId;
    private Integer[] clientList;
    private Boolean[] clientStatusList;

    public MessageType getMessageType() {
        return messageType;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer[] getClientList() {
        return clientList;
    }

    public Boolean[] getClientStatusList() {
        return clientStatusList;
    }

    public MyWebSocketMessage(MessageType messageType, String userNickName, Integer userId, Integer[] clientList,
            Boolean[] clientStatusList) {
        this.messageType = messageType;
        this.userNickName = userNickName;
        this.userId = userId;
        this.clientList = clientList;
        this.clientStatusList = clientStatusList;
    }
}
