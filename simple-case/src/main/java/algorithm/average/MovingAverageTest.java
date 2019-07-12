package algorithm.average;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-7-9 下午3:15 <br/>
 * Desc:
 */
public class MovingAverageTest {

    List<Long> price5, price10;

    @Before
    public void init() {
        price5 = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        price10 = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
    }

    @Test
    public void ma5() {
        sma5();
        wma5();
        ema5();
    }

    @Test
    public void sma5() {
        MovingAverage ma = new SimpleMovingAverageImpl();
        double sma5 = ma.getAverage(price5, 5);
        double newSma5 = ma.refreshAverage(sma5, price5, 6L, 5);
        System.out.println(String.format("sma5: %.2f, new sma5: %.2f", sma5, newSma5));
    }

    @Test
    public void wma5() {
        MovingAverage ma = new WeightedMovingAverageImpl();
        double ma5 = ma.getAverage(price5, 5);
        double newMa5 = ma.refreshAverage(ma5, price5, 6L, 5);
        System.out.println(String.format("wma5: %.2f, new wma5: %.2f", ma5, newMa5));
    }

    @Test
    public void ema5() {
        MovingAverage ma = new ExponentialWeightedMovingAverageImpl();
        double ma5 = ma.getAverage(price5, 5);
        double newMa5 = ma.refreshAverage(ma5, price5, 6L, 5);
        System.out.println(String.format("ewma5: %.2f, new ewma5: %.2f", ma5, newMa5));
    }
}
