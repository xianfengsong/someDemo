package algorithm.average;

import java.util.List;

/**
 * author Xianfeng <br/>
 * date 19-7-9 下午2:43 <br/>
 * Desc:
 * EMA和EWMA是一个意思
 * 指數移動平均（英语：exponential moving average，EMA或EXMA）是以指數式遞減加權的移動平均。
 * 各數值的加權影響力隨時間而指數式遞減，越近期的數據加權影響力越重，但較舊的數據也給予一定的加權值。
 */
public class ExponentialWeightedMovingAverageImpl implements MovingAverage {

    @Override
    public double getAverage(List<Long> priceList, Integer days) {
        double a = (double) 2 / (days + 1);
        int power = days - 1;
        double sum = 0.0d;
        for (Long price : priceList) {
            double weight = Math.pow((1 - a), power);
            sum += weight * price;
            power--;
        }
        double totalWeight = 0.0d;
        for (int i = 0; i < days; i++) {
            totalWeight += Math.pow((1 - a), i);
        }
        return sum / totalWeight;
    }

    @Override
    public double refreshAverage(double average, List<Long> oldPriceList, Long newPrice, Integer days) {
        double a = (double) 2 / (days + 1);
        return average + a * (newPrice - average);
    }
}
