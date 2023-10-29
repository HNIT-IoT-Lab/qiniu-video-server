package cn.hnit.utils.col;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 谢武科
 * @date 2020/11/30 21:45
 */
public class ColUtils {

    private final static String[] constellationArr = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};

    private final static String[] constellations = new String[]{"Capricorn", "Aquarius", "Pisces", "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra", "Scorpio", "Sagittarius", "Capricorn"};


    /**
     * 根据生日获得星座
     *
     * @param yyyy_MM_dd
     * @return
     */
    public static String getConstellation(String yyyy_MM_dd) {
        String date = StringUtils.replace(yyyy_MM_dd, "-", "");
        int month = 0;
        int day = 0;
        try {
            Integer dNumber = Integer.parseInt(date);
            day = dNumber % 10 + (dNumber / 10) % 10 * 10;
            dNumber = dNumber / 100;
            month = dNumber % 10 + (dNumber / 10) % 10 * 10;
        } catch (NumberFormatException e) {
            return "";
        }
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }


    /**
     * 根据生日获得星座英文
     *
     * @param yyyy_MM_dd
     * @return
     */
    public static String getConstellationEn(String yyyy_MM_dd) {
        String date = StringUtils.replace(yyyy_MM_dd, "-", "");
        int month = 0;
        int day = 0;
        try {
            Integer dNumber = Integer.parseInt(date);
            day = dNumber % 10 + (dNumber / 10) % 10 * 10;
            dNumber = dNumber / 100;
            month = dNumber % 10 + (dNumber / 10) % 10 * 10;
        } catch (NumberFormatException e) {
            return "";
        }
        return day < dayArr[month - 1] ? constellations[month - 1] : constellations[month];
    }
}
