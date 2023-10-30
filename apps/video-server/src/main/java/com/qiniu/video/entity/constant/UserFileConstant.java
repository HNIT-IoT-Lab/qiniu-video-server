package com.qiniu.video.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface UserFileConstant {

    @AllArgsConstructor
    @Getter
    enum UserFileKind {
        IMAGE("image", "图片"),
        DOCUMENT("document", "文本"),
        VIDEO("video", "视频"),


        ;

        private final String fileKind;
        private final String fileName;


        public static UserFileKind of(String filePath) {
            String extension = getFileExtension(filePath);

            switch (extension) {
                case ".jpg":
                case ".jpeg":
                case ".png":
                case ".gif":
                case ".webp":
                    return UserFileKind.IMAGE;
                case ".pdf":
                case ".doc":
                case ".docx":
                    return UserFileKind.DOCUMENT;
                case ".mp4":
                case ".avi":
                case ".mov":
                case ".webm":
                    return UserFileKind.VIDEO;
                default:
                    return null; // 或者抛出异常，表示无法识别的文件类型
            }
        }


        static String getFileExtension(String filePath) {
            int dotIndex = filePath.lastIndexOf(".");
            if (dotIndex != -1 && dotIndex < filePath.length() - 1) {
                return filePath.substring(dotIndex);
            }
            return "";
        }
    }
}
