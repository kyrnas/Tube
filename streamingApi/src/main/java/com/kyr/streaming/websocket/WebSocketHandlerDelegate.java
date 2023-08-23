package com.kyr.streaming.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketHandlerDelegate {
    void afterConnectionEstablished(WebSocketSession session, String videoId) throws Exception;
    void afterConnectionClosed(WebSocketSession session, String videoId, CloseStatus closeStatus) throws Exception;
    void sendProgressUpdateToSession(String videoId, String progress);
}
