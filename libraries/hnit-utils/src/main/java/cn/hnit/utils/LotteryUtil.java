package cn.hnit.utils;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
public class LotteryUtil {

    public static int lottery(@NonNull List<Double> rates) {
        if(rates.isEmpty()) {
            return -1;
        }
        double sumRate = rates.stream().mapToDouble(item->item).sum();
        List<Double> tempRates = new ArrayList<>();
        double tempRate = 0.0;
        for (Double rate : rates) {
            tempRate += rate;
            tempRates.add(tempRate / sumRate);
        }
        double random = Math.random();
        tempRates.add(random);
        return tempRates.stream().sorted().collect(Collectors.toList()).indexOf(random);
    }

}
