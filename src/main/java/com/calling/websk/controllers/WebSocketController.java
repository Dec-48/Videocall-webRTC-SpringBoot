package com.calling.websk.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calling.websk.components.MySocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class WebSocketController {
    private MySocketHandler mySocketHandler;

    public WebSocketController(MySocketHandler mySocketHandler) {
        this.mySocketHandler = mySocketHandler;
    };

    @PostMapping("/open-room/{userId}")
    public ResponseEntity<String> openRoom(@PathVariable int userId) throws JsonProcessingException, IOException {
        Boolean result = mySocketHandler.openRoom(userId);
        return ResponseEntity.ok("successfully open room");
        // if (result) {
        // return ResponseEntity.ok("successfully open room");
        // } else {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("already open the
        // room");
        // }
    }

    @PostMapping("/close-room/{userId}")
    public ResponseEntity<String> closeRoom(@PathVariable int userId) throws JsonProcessingException, IOException {
        Boolean result = mySocketHandler.closeRoom(userId);
        return ResponseEntity.ok("successfully close room");
        // if (result) {
        // return ResponseEntity.ok("successfully close room");
        // } else {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("already close the
        // room");
        // }
    }
}
