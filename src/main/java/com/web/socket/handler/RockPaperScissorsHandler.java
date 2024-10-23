package com.web.socket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RockPaperScissorsHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> waitingPlayers = new ConcurrentHashMap<>();
    private final Map<String, String> playerChoices = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, WebSocketSession> matchedPlayers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 플레이어 대기열에 추가
        if (waitingPlayers.isEmpty()) {
            waitingPlayers.put(session.getId(), session);
        } else {
            // 이미 대기 중인 플레이어가 있다면 매칭
            WebSocketSession waitingSession = waitingPlayers.values().iterator().next();
            waitingPlayers.remove(waitingSession.getId());
            matchedPlayers.put(session, waitingSession);
            matchedPlayers.put(waitingSession, session);

            try {
                waitingSession.sendMessage(new TextMessage("매칭되었습니다. 가위/바위/보 중 하나를 선택하세요."));
                session.sendMessage(new TextMessage("매칭되었습니다. 가위/바위/보 중 하나를 선택하세요."));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String choice = message.getPayload().toLowerCase();
        if (!choice.equals("가위") && !choice.equals("바위") && !choice.equals("보")) {
            try {
                session.sendMessage(new TextMessage("잘못된 입력입니다. 가위, 바위, 보 중 하나를 선택하세요."));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        playerChoices.put(session.getId(), choice);

        WebSocketSession opponent = matchedPlayers.get(session);
        if (opponent != null && playerChoices.containsKey(opponent.getId())) {
            String opponentChoice = playerChoices.get(opponent.getId());
            String resultForPlayer = determineWinner(choice, opponentChoice);
            String resultForOpponent = determineWinner(opponentChoice, choice);

            try {
                session.sendMessage(new TextMessage("결과: " + resultForPlayer));
                opponent.sendMessage(new TextMessage("결과: " + resultForOpponent));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 결과 처리 후 초기화
            playerChoices.remove(session.getId());
            playerChoices.remove(opponent.getId());
            matchedPlayers.remove(session);
            matchedPlayers.remove(opponent);
        }
    }

    private String determineWinner(String playerChoice, String opponentChoice) {
        if (playerChoice.equals(opponentChoice)) {
            return "무승부";
        }

        switch (playerChoice) {
            case "가위":
                return (opponentChoice.equals("보")) ? "당신이 이겼습니다!" : "당신이 졌습니다.";
            case "바위":
                return (opponentChoice.equals("가위")) ? "당신이 이겼습니다!" : "당신이 졌습니다.";
            case "보":
                return (opponentChoice.equals("바위")) ? "당신이 이겼습니다!" : "당신이 졌습니다.";
            default:
                return "오류가 발생했습니다.";
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        waitingPlayers.remove(session.getId());
        WebSocketSession opponent = matchedPlayers.remove(session);
        if (opponent != null) {
            matchedPlayers.remove(opponent);
            try {
                opponent.sendMessage(new TextMessage("상대방이 연결을 종료했습니다."));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
