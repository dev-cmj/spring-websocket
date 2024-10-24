package com.web.socket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.socket.domain.StockPrice;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StockPriceService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public StockPriceService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public StockPrice getStockPrice(String symbol) {
        // 네이버 금융 API 호출 URL
        String url = "https://api.finance.naver.com/siseJson.naver?symbol=" + symbol +
                "&requestType=1&timeframe=week";

        try {
            // API 호출 후 결과를 String으로 받음 (JSONP 형식이므로 String 처리)
            String jsonpResponse = restTemplate.getForObject(url, String.class);

            // JSONP에서 순수 JSON만 추출 todo:해당 코드에서 에러 발생 수정 예정
            String jsonResponse = extractJsonFromJsonp(jsonpResponse);

            // JSON 파싱
            List<Object> response = objectMapper.readValue(jsonResponse, List.class);

            if (response != null && response.size() > 1) {
                // API에서 받은 데이터 중 최신 주식 정보 추출 (맨 위에는 표)
                List<Object> latestData = (List<Object>) response.get(1);

                String date = (String) latestData.get(0);
                double closePrice = ((Number) latestData.get(4)).doubleValue();  // 종가
                return new StockPrice(symbol, closePrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new StockPrice(symbol, 0);
    }

    // JSONP에서 JSON 부분만 추출하는 메서드
    private String extractJsonFromJsonp(String jsonpResponse) {
        Pattern pattern = Pattern.compile("\\((.*)\\)");
        Matcher matcher = pattern.matcher(jsonpResponse);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return jsonpResponse;
    }
}

