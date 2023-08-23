package com.kyr.streaming.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
public class ProgressTrackingWebSocketHandler extends TextWebSocketHandler {
    private final WebSocketHandlerDelegate webSocketHandlerDelegate;

    public ProgressTrackingWebSocketHandler(WebSocketHandlerDelegate webSocketHandlerDelegate) {;
        this.webSocketHandlerDelegate = webSocketHandlerDelegate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String videoId = extractVideoId(session);
        webSocketHandlerDelegate.afterConnectionEstablished(session, videoId);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String videoId = extractVideoId(session);
        webSocketHandlerDelegate.afterConnectionClosed(session, videoId, closeStatus);
        super.afterConnectionClosed(session, closeStatus);
    }

    private String extractVideoId(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        Object videoIdObj = attributes.get("videoId"); // Assuming "videoId" is the attribute key set during WebSocket connection
        if (videoIdObj instanceof String) {
            return (String) videoIdObj;
        } else {
            return null;
        }
    }
}
