package com.kyr.streaming.configuration;

import com.kyr.streaming.websocket.ProgressTrackingWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ProgressTrackingWebSocketHandler progressWebSocketHandler;

    @Autowired
    public WebSocketConfig(ProgressTrackingWebSocketHandler progressWebSocketHandler) {
        this.progressWebSocketHandler = progressWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(progressWebSocketHandler, "/progress/{videoId}")
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketInterceptor());
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor();
    }

    private class WebSocketInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            // Get the videoId from the URI path and set it as an attribute in the WebSocket session
            String path = request.getURI().getPath();
            String videoId = extractVideoIdFromPath(path); // Implement this method to extract the video ID from the path
            attributes.put("videoId", videoId);
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            // No need for any post-handshake action
        }
    }

    private String extractVideoIdFromPath(String path) {
        // Implement this method to extract the video ID from the URI path.
        // For example, if the path is "/progress/123", this method should return 123 as a Long.
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
