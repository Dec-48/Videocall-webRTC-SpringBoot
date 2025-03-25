package com.calling.websk.config;

import io.micrometer.common.lang.NonNull;

import org.springframework.context.annotation.Bean;
// import org.springframework.beans.factory.annotation.Autowired; // Unnecessary
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.calling.websk.components.MySocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    @SuppressWarnings("null")
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(mySocketHandler(), "/socket")
                .setAllowedOrigins("*");
    }

    @Bean
    public MySocketHandler mySocketHandler() {
        return new MySocketHandler();
    }

}