package com.calling.websk.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.calling.websk.components.MySocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class WebSocketController {
    private MySocketHandler mySocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // @Autowired
    public WebSocketController(MySocketHandler mySocketHandler){
        this.mySocketHandler = mySocketHandler;
    }

    @GetMapping("/active-clients")
    ResponseEntity<String> getActiveClients() {
        String json;
		try {
			json = objectMapper.writeValueAsString(mySocketHandler.getMap());
            return ResponseEntity.ok().body(
                json
            );
		} catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                e.toString()
            );
		}
    }

}
