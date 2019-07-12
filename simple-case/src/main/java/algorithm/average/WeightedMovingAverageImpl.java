package algorithm.average;

import java.util.List;
import org.junit.Assert;

/**
 * author Xianfeng <br/>
 * date 19-7-9 上午11:28 <br/>
 * Desc:
 * 加權移動平均（英语：weighted moving average，WMA）指計算平均值時將個別數據乘以不同數值，
 * 在技術分析中，n日WMA的最近期一個數值乘以n、次近的乘以n-1，如此類推，一直到0。
 */
public class WeightedMovingAverageImpl implements MovingAverage {

    @Override
    public double getAverage(List<Long> priceList, Integer days) {
        Assert.assertEquals("price和天数不一致", (int) days, priceList.size());
        double totalWeight = days * (days + 1) / 2;
        Long sum = 0L;
        //越新的价格权重越高
        int weight = 1;
        for (Long price : priceList) {
            sum += price * weight;
            weight++;
        }
        return sum / totalWeight;
    }

    @Override
    public double refreshAverage(double average, List<Long> oldPriceList, Long newPrice, Integer days) {
        Assert.assertEquals("price和天数不一致", (int) days, oldPriceList.size());
        double totalWeight = days * (days + 1) / 2;

        Long oldSum = 0L;
        int weight = 1;
        for (Long price : oldPriceList) {
            oldSum += price * weight;
            weight++;
        }
        Long sum = oldSum + days * newPrice - oldPriceList.stream().mapToLong(Long::valueOf).sum();
        return sum / totalWeight;
    }
}
