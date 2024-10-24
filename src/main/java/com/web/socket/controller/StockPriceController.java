package com.web.socket.controller;

import com.web.socket.domain.StockPrice;
import com.web.socket.domain.StockRequest;
import com.web.socket.service.StockPriceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class StockPriceController {

    private final SimpMessagingTemplate messagingTemplate;
    private final StockPriceService stockPriceService;

    public StockPriceController(SimpMessagingTemplate messagingTemplate, StockPriceService stockPriceService) {
        this.messagingTemplate = messagingTemplate;
        this.stockPriceService = stockPriceService;
    }

//    @Scheduled(fixedRate = 10000)  // 10초마다 주식 가격 업데이트
    public void updateStockPrices() {
        String[] stockSymbols = {"005930", "035720", "035420"};  // 삼성전자, 카카오, 네이버
        for (String symbol : stockSymbols) {
            StockPrice stockPrice = stockPriceService.getStockPrice(symbol);
            messagingTemplate.convertAndSend("/topic/price/" + symbol, stockPrice);
        }
    }



}
