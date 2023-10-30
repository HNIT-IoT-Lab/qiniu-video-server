package com.qiniu.video.utils;

import java.util.Random;

public class RandomUsernameGenerator {
    private static final String[] adjectives = {"很慌得", "非常开心的", "快乐的", "勇敢的", "聪明的"};
    private static final String[] animals = {"大狮子", "小兔子", "小猫咪", "大象", "小鸟"};
    private static final String[] places = {"在草原", "在森林", "在海洋", "在花园", "在山顶"};
    private static final String[] actions = {"放羊", "跳舞", "唱歌", "冒险", "探险"};

    public static String generateRandomUsername() {
        Random random = new Random();

        String adjective = adjectives[random.nextInt(adjectives.length)];
        String animal = animals[random.nextInt(animals.length)];
        String place = places[random.nextInt(places.length)];
        String action = actions[random.nextInt(actions.length)];

        return adjective + animal + place + action;
    }

    public static void main(String[] args) {
        String randomUsername = generateRandomUsername();
        System.out.println(randomUsername);
    }
}