package com.web.socket.domain;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class OmokRoom {
    private WebSocketSession player1;
    private WebSocketSession player2;
    private String[][] board = new String[19][19]; // 19x19 오목판
    private boolean isPlayer1Turn = true;
    private boolean gameEnded = false; // 게임 종료 여부

    public OmokRoom(String s) {
    }

    // 플레이어를 방에 추가
    public void addPlayer(WebSocketSession session) throws IOException {
        if (player1 == null) {
            player1 = session;
            session.sendMessage(new TextMessage("MESSAGE,당신은 흑돌입니다."));
        } else if (player2 == null) {
            player2 = session;
            session.sendMessage(new TextMessage("MESSAGE,당신은 백돌입니다."));
        } else {
            session.sendMessage(new TextMessage("MESSAGE,이미 두 명의 플레이어가 게임에 참가 중입니다."));
        }
    }

    // 플레이어를 방에서 제거
    public void removePlayer(WebSocketSession session) throws IOException {
        if (player1 != null && player1.equals(session)) {
            player1 = null;
        } else if (player2 != null && player2.equals(session)) {
            player2 = null;
        }
    }
    

    // 플레이어가 착수한 수를 처리
    public void processMove(WebSocketSession session, String move) throws IOException {
        int row = Integer.parseInt(move.split(",")[1]);
        int col = Integer.parseInt(move.split(",")[2]);

        // 게임이 끝났는지 확인
        if (gameEnded) {
            session.sendMessage(new TextMessage("MESSAGE,게임이 이미 종료되었습니다."));
            return;
        }

        // 플레이어가 모두 접속했는지 확인
        if (player1 == null || player2 == null) {
            session.sendMessage(new TextMessage("MESSAGE,두 명의 플레이어가 모두 접속해야 게임을 시작할 수 있습니다."));
            return;
        }

        // 현재 차례인 플레이어가 맞는지 확인
        if ((isPlayer1Turn && session.equals(player1)) || (!isPlayer1Turn && session.equals(player2))) {

            // 이미 둔 자리가 아닌지 확인
            if (board[row][col] != null) {
                session.sendMessage(new TextMessage("MESSAGE,이미 둔 자리입니다."));
                return;
            }

            // 착수 처리 (흑돌과 백돌 표시)
            String color = isPlayer1Turn ? "흑돌" : "백돌";
            board[row][col] = color;
            isPlayer1Turn = !isPlayer1Turn; // 턴 변경

            broadcastMove(row, col, color); // 착수된 좌표 전송

            // 승리 조건 확인
            if (checkWinCondition(row, col)) {
                String winner = board[row][col]; // 흑돌 또는 백돌 승리
                broadcastResult("승리자: " + winner);
                gameEnded = true;
            }
        } else {
            session.sendMessage(new TextMessage("MESSAGE,상대방의 차례입니다."));
        }
    }

    private void broadcastMove(int row, int col, String color) throws IOException {
        TextMessage message = new TextMessage("MOVE," + row + "," + col + "," + color);
        if (player1 != null && player1.isOpen()) {
            player1.sendMessage(message);
        }
        if (player2 != null && player2.isOpen()) {
            player2.sendMessage(message);
        }
    }

    private void broadcastResult(String result) throws IOException {
        TextMessage resultMessage = new TextMessage("MESSAGE," + result);
        if (player1 != null && player1.isOpen()) {
            player1.sendMessage(resultMessage);
        }
        if (player2 != null && player2.isOpen()) {
            player2.sendMessage(resultMessage);
        }
    }

    // 5개의 돌이 연속으로 놓였는지 확인
    private boolean checkWinCondition(int row, int col) {
        return checkDirection(row, col, 1, 0) ||  // 가로
                checkDirection(row, col, 0, 1) ||  // 세로
                checkDirection(row, col, 1, 1) ||  // 대각선 \
                checkDirection(row, col, 1, -1);   // 대각선 /
    }

    private boolean checkDirection(int row, int col, int dRow, int dCol) {
        String color = board[row][col];
        int count = 1;

        // 한쪽 방향으로 체크
        for (int i = 1; i < 5; i++) {
            int r = row + dRow * i;
            int c = col + dCol * i;
            if (r >= 0 && r < 19 && c >= 0 && c < 19 && board[r][c] == color) {
                count++;
            } else {
                break;
            }
        }

        // 반대쪽 방향으로 체크
        for (int i = 1; i < 5; i++) {
            int r = row - dRow * i;
            int c = col - dCol * i;
            if (r >= 0 && r < 19 && c >= 0 && c < 19 && board[r][c] == color) {
                count++;
            } else {
                break;
            }
        }

        return count >= 5;
    }
}
