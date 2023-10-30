package org.cn.hnit.video.kodo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileNameGenerator {
    public static String generateFileName(String originalFileName) {
        // 获取当前日期时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        // 生成随机数
        Random random = new Random();
        int randomNum = random.nextInt(10000);

        // 组合文件名
        String extension = getFileExtension(originalFileName);

        return timestamp  + randomNum + extension;
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    public static void main(String[] args) {
        String originalFileName = "example.jpg";
        String generatedFileName = generateFileName(originalFileName);
        System.out.println(generatedFileName);
    }

}
