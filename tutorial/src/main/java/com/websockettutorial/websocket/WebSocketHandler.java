package com.websockettutorial.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    /*
    웹 소켓 최초 연결 시 Map 에 세션아이디와 세션을 저장해두고 접속중인 모든 세션에 메세지를 보내주는 로직을 작성한다.
    이것은 채팅방에 이미 들어와있는 사용자들에게 새로운 멤버가 들어왔다는 것을 알려주는 것과 같다.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String sessionId = session.getId();
        sessions.put(sessionId, session);

        Message message = Message.builder().sender(sessionId).receiver("all").build();
        message.newConnect();

        sessions.values().forEach(s -> {
            try {
                if (!s.getId().equals(sessionId)) {
                    s.sendMessage(new TextMessage("connect" + sessionId));
                }
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /*
    handleTextMessage 구현
    수신자가 존재하고 연결된 상태라면 메시지를 전송한다.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        Message message = mapper.readValue(textMessage.getPayload(), Message.class);
        message.setSender(session.getId());

        WebSocketSession receiver = sessions.get(message.getReceiver());
        if (receiver != null && receiver.isOpen()) {
            receiver.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
