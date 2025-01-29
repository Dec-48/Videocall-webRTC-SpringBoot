package com.calling.websk.config;

import com.calling.websk.compo.MySocketHandler;

// import org.springframework.beans.factory.annotation.Autowired; // Unnecessary
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MySocketHandler(), "/socket")
          .setAllowedOrigins("*");
    }
}