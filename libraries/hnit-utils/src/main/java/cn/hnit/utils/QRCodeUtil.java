//package cn.hnit.utils;
//
//import com.beust.jcommander.internal.Maps;
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.Map;
//
///**
// * google生成二维码工具
// */
//public class QRCodeUtil {
//
//    private static final int WIDTH = 300;
//    private static final int HEIGTH = 300;
//    private static final String FORMAT = "png";
//    private static final Map<EncodeHintType, Object> HINTS = Maps.newHashMap();
//
//    static {
//        HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//        HINTS.put(EncodeHintType.MARGIN, 2);
//    }
//    /**
//     * 返回一个 BufferedImage 对象
//     * @param content 二维码内容
//     * @param width   宽
//     * @param height  高
//     */
//    public static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException, IOException {
//        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
//        return MatrixToImageWriter.toBufferedImage(bitMatrix);
//    }
//
//    /**
//     * 将二维码图片输出到一个流中
//     * @param content 二维码内容
//     * @param stream  输出流
//     * @param width   宽
//     * @param height  高
//     */
//    public static void writeToStream(String content, OutputStream stream, int width, int height) throws WriterException, IOException {
//        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
//        MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, stream);
//
//    }
//
//    /**
//     * 将二维码图片输出到一个流中
//     * @param content 二维码内容
//     * @param stream  输出流
//     */
//    public static void writeToStream(String content, OutputStream stream) throws WriterException, IOException {
//        writeToStream(content, stream, WIDTH, HEIGTH);
//    }
//
//    /**
//     * 生成二维码图片文件
//     * @param content 二维码内容
//     * @param path    文件保存路径
//     * @param width   宽
//     * @param height  高
//     */
//    public static File createQRCode(String content, String path, int width, int height) throws WriterException, IOException {
//        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
//        File file = new File(path);
//        MatrixToImageWriter.writeToPath(bitMatrix, FORMAT,file.toPath());
//        return file;
//    }
//
//    /**
//     * 生成二维码图片文件
//     * @param content 二维码内容
//     * @param path    文件保存路径
//     */
//    public static File createQRCode(String content, String path) throws WriterException, IOException {
//        return createQRCode(content, path, WIDTH, HEIGTH);
//    }
//}
