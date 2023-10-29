package cn.hnit.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class ImageUtil {

    public static final String IMAGE_BUFFER = "/tmp/";

    /**
     * 获取网络图片的宽和高
     * @param url
     * @return
     */
    public static String getWidthAndHeight(String url) {
        try {
            InputStream is = new URL(url).openStream();
            BufferedImage sourceImg = ImageIO.read(is);
            int width = sourceImg.getWidth();
            int height = sourceImg.getHeight();
            return width+"*"+height;
        } catch (IOException e) {
            log.error("getWidthAndHeight",e);
        }catch (NullPointerException e) {
            return getWebpWidthAndHeight(url);
        }
        return "1*1";
    }

    public static String getWebpWidthAndHeight(String url) {
        try(InputStream file = new URL(url).openStream()){
            byte[] bytes = new byte[30];
            file.read(bytes,0,bytes.length);
            int width = ((int) bytes[27] & 0xff)<<8 | ((int) bytes[26] & 0xff);
            int height = ((int) bytes[29] & 0xff)<<8 | ((int) bytes[28] & 0xff);
            return width+"*"+height;
        }catch (Exception e) {
            log.error("getWebpWidthAndHeight error", e);
            return "1*1";
        }
    }

    /**
     *
     * @Description:保存图片并且生成缩略图
     * @param filePathName 图片url
     * @param imageQuality
     * @return 图片路径
     */
    public static String uploadFileAndCreateThumbnail(String filePathName, Integer imageSize, Float imageQuality) {
        if(filePathName == null ){
            return null;
        }
        byte[] btImg = getImageFromNetByUrl(filePathName);
        // 如果小于指定大小时直接返回 by ck 2019-4-26
        if(null == btImg ||  (btImg.length / 1024) < imageSize){
            return null;
        }
        log.info("读取到：" + btImg.length + " 字节");
        String fileName = IMAGE_BUFFER + System.currentTimeMillis()+".jpg";
        writeImageToDisk(btImg, fileName);
        /**
         * 缩略图begin
         */
        String thumbnailFilePathName = IMAGE_BUFFER + System.currentTimeMillis()+"aa";
        try {
            Thumbnails.of(fileName).scale(1f).outputQuality(imageQuality).outputFormat("jpg").toFile(thumbnailFilePathName);
        } catch (Exception e1) {
            log.error(ImageUtil.class.getSimpleName()+"图片保存异常",e1);
            return null;
        }

        // 压缩后的图
        File file = new File(thumbnailFilePathName + ".jpg");
        // 压缩之后还大于指定尺寸，进行等比例缩放
        if (file.exists() && (file.length() / 1024) > imageSize) {
            try {
                if (imageSize == 50) {
                    Thumbnails.of(fileName).scale(0.5f).outputQuality(imageQuality).outputFormat("jpg").toFile(thumbnailFilePathName);
                }else if(imageSize == 80){
                    Thumbnails.of(fileName).scale(0.5f).outputQuality(imageQuality).outputFormat("jpg").toFile(thumbnailFilePathName);
                }else {
                    Thumbnails.of(fileName).scale(0.6f).outputQuality(imageQuality).outputFormat("jpg").toFile(thumbnailFilePathName);
                }
            } catch (Exception e) {
                log.error("图片压缩异常",e);
                return null;
            }
        }
        File originalFile = new File(fileName);
        if (originalFile.exists()) {
            log.info("文件名称：{}" , file.getName() + " :是否存在：" + file.exists() + "文件路径：" + file.getPath());
            log.info("文件删除是否成功：{}" , originalFile.delete());
        }
        return thumbnailFilePathName + ".jpg";
    }

    /**
     * 根据地址获得数据的字节流
     * @param strUrl 网络连接地址
     * @return
     */
    public static byte[] getImageFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
            return btImg;
        } catch (Exception e) {
            log.info("获取网络图片异常",e);
        }
        return null;
    }


    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


    /**
     * 将图片写入到磁盘
     * @param img 图片数据流
     * @param fileName 文件保存时的名称
     */
    public static void writeImageToDisk(byte[] img, String fileName){
        FileOutputStream fops = null;
        try {
            File file = new File( fileName);
            fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();

        } catch (Exception e) {
           log.error("图片写入异常",e);
        }finally {
            try {
                if(fops!=null) fops.close();
            } catch (IOException e) {
                log.info("网络图写入异常",e);
            }
        }
    }

    /**
     * 判斷是否有表情
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                        || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }
}
