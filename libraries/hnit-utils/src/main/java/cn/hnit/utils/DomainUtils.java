package cn.hnit.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author 谢武科
 * @date 2021/1/21 21:39
 */
@Slf4j
public class DomainUtils {


    public static String fetchUrlNoDomain(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        String regexFirst = "//";
        String[] firsts = url.split(regexFirst);
        if (firsts != null && firsts.length >= 2) {
            url = firsts[1];
        }
        String regex = "/";
        String[] strings = url.split(regex);
        if (strings != null && strings.length >= 2) {
            //输出结果
            log.info(strings[1]);
        }
        return url;
    }

    public static void main(String[] args) {
        fetchUrlNoDomain("http://a.com/a.jpg");
    }


}
