package cn.hnit.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 数值操作
 */
public class NumberUtil {

    public static final Double TEN_THOUSAND = 10000.0;

    public static final Double THOUSAND = 1000.0;

    public static String numUnitFmt(Long value,NumUnit minUnit) {
        String loveValue;
        if (value >= TEN_THOUSAND && value < THOUSAND * TEN_THOUSAND) {
            loveValue = new DecimalFormat("0.0").format(value / TEN_THOUSAND) + "w";
        } else if (value >= THOUSAND && value < TEN_THOUSAND&&minUnit== NumUnit.K) {
            loveValue = new DecimalFormat("0.0").format(value / THOUSAND) + "k";
        } else if (value >= THOUSAND * TEN_THOUSAND) {
            loveValue ="999.9w";
        } else {
            loveValue =value + "";
        }
        return loveValue;
    }

    public static String numUnitFmt2(Long value,NumUnit minUnit) {
        String loveValue;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        if (value >= TEN_THOUSAND) {
            loveValue = df.format(value / TEN_THOUSAND) + "w";
        } else if (value >= THOUSAND && value < TEN_THOUSAND&&minUnit== NumUnit.K) {
            loveValue = df.format(value / THOUSAND) + "k";
        } else {
            loveValue =value + "";
        }
        return loveValue;
    }

    @Getter
    @AllArgsConstructor
    public enum NumUnit{
        //单位装换
        K("k", "千转换"),
        W("w", "万转换"),
        ;
        private final String code;
        private final String desc;
    }

    public static  <T> List<T> randomlyGetSpecifiedQuantityFromList(List<T> list, int number){
        List<Integer> selectedList = randomlyGetMNonRepeatingNumbersFromZeroToN(number, list.size());
        return selectedList.stream().map(num -> {
            return list.get(num);
        }).collect(Collectors.toList());
    }
    //从[0,n)中随机的获取m个数
    public static List<Integer> randomlyGetMNonRepeatingNumbersFromZeroToN(int m,int n){
        int[] countArray = new int[n];
        int noSelected=0,hasSelected=1,countNumber=0;
        Random random = new Random();
        List<Integer> result = new ArrayList<>();
        //优化。如果说m大于n/2，那我就取出n-m个，将没有被取的收集起来。目的：减少while的循环次数
        boolean flag = m > n / 2;
        m=flag?m-n:n;
        while (countNumber<m){
            int index = random.nextInt(n);
            if (countArray[index]==noSelected){
                ++countArray[index];
                ++countNumber;
            }
        }
        for (int index : countArray) {
            if (flag){
                if (countArray[index]==noSelected){
                    result.add(index);
                }
            }else {
                if (countArray[index]==hasSelected){
                    result.add(index);
                }
            }
        }
        return result;
    }

}
