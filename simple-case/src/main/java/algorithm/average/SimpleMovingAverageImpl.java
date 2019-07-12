package algorithm.average;

import java.util.List;
import org.junit.Assert;

/**
 * author Xianfeng <br/>
 * date 19-7-9 上午10:59 <br/>
 * Desc: 簡單移動平均（英语：simple moving average，SMA）是某變數之前n個數值的未作加權算術平均
 */
public class SimpleMovingAverageImpl implements MovingAverage {

    @Override
    public double getAverage(List<Long> priceList, Integer days) {
        Long sum = priceList.stream().mapToLong(Long::valueOf).sum();
        Assert.assertEquals("price和天数不一致", (int) days, priceList.size());
        return sum / days;
    }

    @Override
    public double refreshAverage(double average, List<Long> oldPriceList, Long newPrice, Integer days) {
        double oldestAverage = oldPriceList.get(0) / days;
        double newAverage = newPrice / days;
        return average - oldestAverage + newAverage;
    }
}
