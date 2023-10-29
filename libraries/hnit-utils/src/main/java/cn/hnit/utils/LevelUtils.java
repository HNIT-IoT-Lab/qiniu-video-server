package cn.hnit.utils;

import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class LevelUtils {

    public static String[] getLevel(List<String[]> levels, double score) {
        String[] rs = {"1", "0",""};
        for (int i = levels.size() - 1; i >= 0; i--) {
            String[] arr = levels.get(i);
            long value = Long.parseLong(arr[1]);
            if (score >= value) {
                rs[0] = arr[0];
                if (i == levels.size() - 1) {
                    rs[1] = "1";
                } else {
                    String[] arr2 = levels.get(i + 1);
                    rs[1] = NumberUtil.decimalFormat("0.00", (score - value) / (Long.parseLong(arr2[1]) - value));
                }
                break;
            }
        }
        rs[2] = Hat.valueOf("LV" + rs[0]).hatUrl;
        return rs;
    }

    @AllArgsConstructor
    @Getter
    public enum Hat{
        LV1("1","https://cdnpw.chinaesport.com/cms/companion/16025776200092427613125028169.png","https://cdnpw.chinaesport.com/cms/companion/16028386891416134440166804085.png"),
        LV2("2","https://cdnpw.chinaesport.com/cms/companion/16025776557732427652891482043.png","https://cdnpw.chinaesport.com/cms/companion/16028387040896134455125961438.png"),
        LV3("3","https://cdnpw.chinaesport.com/cms/companion/16025776749322427666070702636.png","https://cdnpw.chinaesport.com/cms/companion/16028387264516134472487905943.png"),
        LV4("4","https://cdnpw.chinaesport.com/cms/companion/16025776996452427691783412988.png","https://cdnpw.chinaesport.com/cms/companion/16028387333446134484380189483.png"),
        LV5("5","https://cdnpw.chinaesport.com/cms/companion/16025777166712427707809461011.png","https://cdnpw.chinaesport.com/cms/companion/16028387425566134495592576166.png"),
        ;

        private String level;
        private String hatUrl;
        private String h5Url;
    }
}
