package com.web.socket.generator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NicknameGenerator {

    private static final List<String> adjectives = Arrays.asList(
            "멋진", "용감한", "빠른", "지혜로운", "행복한", "귀여운", "강력한", "조용한", "재미있는", "빛나는"
    );

    private static final List<String> animals = Arrays.asList(
            "호랑이", "독수리", "사자", "늑대", "곰", "용", "여우", "팬더", "고양이", "강아지"
    );

    private static final List<String> objects = Arrays.asList(
            "별", "꽃", "바람", "산", "강", "달", "구름", "나무", "돌", "불"
    );

    private static final Random random = new Random();

    public static String createRandomNickName() {
        String adjective = getRandomElement(adjectives);  // 랜덤 형용사
        String noun = random.nextBoolean() ? getRandomElement(animals) : getRandomElement(objects);  // 랜덤 동물 또는 사물
        int randomNumber = random.nextInt(100);  // 0~99 사이의 랜덤 숫자
        return adjective + noun + randomNumber;  // 형용사 + 명사 + 숫자 조합
    }

    private static String getRandomElement(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}