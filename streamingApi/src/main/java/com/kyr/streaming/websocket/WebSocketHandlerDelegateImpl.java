package com.kyr.streaming.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandlerDelegateImpl implements WebSocketHandlerDelegate {
    private final Map<String, Set<WebSocketSession>> videoSessionsMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session, String videoId) throws Exception {
        if (videoId != null) {
            videoSessionsMap.computeIfAbsent(videoId, key -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, String videoId, CloseStatus closeStatus) throws Exception {
        if (videoId != null) {
            Set<WebSocketSession> sessions = videoSessionsMap.get(videoId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    @Override
    public void sendProgressUpdateToSession(String videoId, String progress) {
        Set<WebSocketSession> sessions = videoSessionsMap.get(videoId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                sendProgressUpdateToSession(session, progress);
            }
        }
    }

    // Method to send progress updates to a specific WebSocket session
    private void sendProgressUpdateToSession(WebSocketSession session, String progress) {
        try {
            session.sendMessage(new TextMessage(progress));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
