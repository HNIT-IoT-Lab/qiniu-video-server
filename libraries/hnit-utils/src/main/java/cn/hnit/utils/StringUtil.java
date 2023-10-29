package cn.hnit.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 *
 * @author Admin
 */
@Slf4j
public class StringUtil {

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 字符串拼接
     *
     * @param delimiter 分隔符
     * @param list
     * @return
     */
    public static String join(CharSequence delimiter, List<?> list) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object ob : list) {
            joiner.add(String.valueOf(ob));
        }
        return joiner.toString();
    }

    /**
     * 截取前len个字符
     *
     * @param source
     * @param len
     * @return
     */
    public static String subStringButRetainEmoji(String source, int len) {
        if (source == null || source.length() < (len + 1) || len < 2) {
            return source;
        }
        int target = len - 1;
        /**
         * 如果第target个字符包是表情字符的开始(一个表情两个字符)
         */
        if (containsEmoji(source, target)) {
            return source.substring(0, target + 2);
        } else {
            return source.substring(0, target + 1);
        }
    }

    /**
     * 截取前len个字符并替换
     *
     * @param source
     * @param len
     * @return
     */
    public static String replaceButRetainEmoji(String source, int len, String replaceStr) {
        return StringUtil.subStringButRetainEmoji(source, 4) + replaceStr;
    }

    /**
     * 截取前n个字符串，超出部分用 replaceStr 替换  比如 xxx...
     * @return
     */
    public static String replaceRetainEmojiBeyondLength(String source,int len,String replaceStr){
        if(StringUtils.isBlank(source)){
            return "";
        }
        String result = StringUtil.subStringButRetainEmoji(source, len);
        //判断是否有截取 如果截取了 就替换
        if(source.length()>result.length()){
            return result+replaceStr;
        }
        return result;
    }


    /**
     * 判断target的位置，是不是表情字符码第一个字符的开始
     * 一个表情有两个字符，target代码表情的第一个字符的位置
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean containsEmoji(String source, int target) {
        boolean isEmoji = false;
        char hs = source.charAt(target);
        if (0xd800 <= hs && hs <= 0xdbff) {
            if (source.length() > 1 && target < source.length() - 1) {
                char ls = source.charAt(target + 1);
                int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                if (0x1d000 <= uc && uc <= 0x1f77f) {
                    isEmoji = true;
                }
            }
        } else {
            // non surrogate
            if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                isEmoji = true;
            } else if (0x2B05 <= hs && hs <= 0x2b07) {
                isEmoji = true;
            } else if (0x2934 <= hs && hs <= 0x2935) {
                isEmoji = true;
            } else if (0x3297 <= hs && hs <= 0x3299) {
                isEmoji = true;
            } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                    || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                    || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                isEmoji = true;
            }
            if (!isEmoji && source.length() > 1 && target < source.length() - 1) {
                char ls = source.charAt(target + 1);
                if (ls == 0x20e3) {
                    isEmoji = true;
                }
            }
        }
        return isEmoji;
    }

    public static String subStringButRetainEmoji(String source, int len, boolean userPoint) {
        if (source == null || source.length() < (len + 1) || len < 2) {
            return source;
        }
        int target = len - 1;
        /**
         * 如果第target个字符包是表情字符的开始(一个表情两个字符)
         */
        if (containsEmoji(source, target)) {
            return userPoint ? source.substring(0, target + 2) + "..." : source.substring(0, target + 2);
        } else {
            return userPoint ? source.substring(0, target + 1) + "..." : source.substring(0, target + 1);
        }
    }

    private static final Pattern reg = Pattern.compile("\\$\\{\\w+\\}");

    public static String format(String template, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        Matcher m = reg.matcher(template);
        while (m.find()) {
            String param = m.group();
            Object value = params.get(param.substring(2, param.length() - 1));
            m.appendReplacement(sb, value == null ? "" : value.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String splitBrace(String content) {
        if (StringUtils.isBlank(content)) return "";
        Pattern p = Pattern.compile("\\{\\{([^}]*)\\}\\}");
        Matcher m = p.matcher(content);
        while (m.find()) {
            content = content.replace(m.group().substring(1, m.group().length() - 1), "");
        }

        return content;
    }

    public static String formatBalance(long balance) {
        StringBuilder balanceStr = new StringBuilder();
        String str = String.valueOf(balance);
        int mark = str.length() % 3;
        for (int i = 0; i < str.length(); i++) {
            if(i == mark) {
                if(i != 0) balanceStr.append(",");
                mark = mark + 3;
            }
            balanceStr.append(str.charAt(i));
        }

        return balanceStr.toString();
    }

    public static String encryPhone(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() != 11) {
            return "";
        }
        String head = phone.substring(0, 3);
        String end = phone.substring(7, 11);
        StringBuilder str = new StringBuilder();
        str.append(head);
        str.append("****");
        str.append(end);

        return str.toString();
    }

    public static String encryIdCardNumber(String idCard) {
        if (StringUtils.isNotBlank(idCard)) {
            if (idCard.length() == 15) {
                idCard = idCard.replaceAll("(\\w{6})(\\w{6})(\\w{3})", "******$2***");
            }
            if (idCard.length() == 18) {
                idCard = idCard.replaceAll("(\\w{6})(\\w{8})(\\w{4})", "******$2****");
            }
        }
        return idCard;
    }

    public static void main(String[] args) {
        System.out.println(formatBalance(3453453600l));
    }
}
