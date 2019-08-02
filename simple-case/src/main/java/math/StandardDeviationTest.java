package math;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-8-2 下午5:24 <br/>
 * Desc: 求标准差
 */
public class StandardDeviationTest {

    /**
     * 根据正太分布，取TP99平均响应时间+3倍标准差做响应时间,可以覆盖99.9973%的TP99响应时间样本
     * 最终会超过响应时间的请求比例估计 (1-.99) * (1-.9973) = 0.0027%
     */
    @Test
    public void get3oTimeout() {
        String json = "[[1563721200,870.908167],[1563732000,454.4],[1563742800,162.227778],[1563753600,294.761111],[1563764400,643.411111],[1563775200,286.453722],[1563786000,210.039944],[1563796800,261.609778],[1563807600,293.825056],[1563818400,216.615389],[1563829200,96.588889],[1563840000,167.966667],[1563850800,299.927778],[1563861600,479.303222],[1563872400,403.835167],[1563883200,466.265889],[1563894000,501.932889],[1563904800,239.516667],[1563915600,130.216667],[1563926400,136.205556],[1563937200,259.422222],[1563948000,282.553556],[1563958800,280.290222],[1563969600,225.672389],[1563980400,245.850833],[1563991200,213.866667],[1564002000,100.888889],[1564012800,148.372222],[1564023600,228.338889],[1564034400,253.169111],[1564045200,269.129056],[1564056000,305.952],[1564066800,347.4145],[1564077600,198.722222],[1564088400,130.372222],[1564099200,180.461111],[1564110000,267.462333],[1564120800,291.791722],[1564131600,325.745389],[1564142400,324.206333],[1564153200,373.637611],[1564164000,264.117556],[1564174800,139.738889],[1564185600,154.511111],[1564196400,255.036444],[1564207200,323.353778],[1564218000,459.711778],[1564228800,345.810222],[1564239600,402.333889],[1564250400,305.076111],[1564261200,178.827778],[1564272000,253.533333],[1564282800,301.405556],[1564293600,343.073222],[1564304400,349.937222],[1564315200,451.179722],[1564326000,402.467444],[1564336800,263.669778],[1564347600,163.533333],[1564358400,165.95],[1564369200,325.823444],[1564380000,415.522389],[1564390800,433.382556],[1564401600,291.066278],[1564412400,219.304667],[1564423200,196.978333],[1564434000,107.555556],[1564444800,144.927778],[1564455600,280.277222],[1564466400,268.871722],[1564477200,212.673389],[1564488000,259.268167],[1564498800,274.257556],[1564509600,155.949111],[1564520400,144.544444],[1564531200,150.9],[1564542000,234.041944],[1564552800,264.071722],[1564563600,348.820444],[1564574400,328.552667]]";
        JSONArray array = JSON.parseArray(json);
        List<BigDecimal> values = new ArrayList<>(array.size());
        for (Object a : array.toArray()) {
            JSONArray jsonArray = (JSONArray) a;
            BigDecimal v = jsonArray.getBigDecimal(1);
            values.add(v);
        }
        double avg = getAvg(values);
        double std = getStdDEV(values);
        System.out.println("平均数：" + avg);
        System.out.println("标准差：" + std);
        System.out.println("响应时间：" + (std * 3 + avg));
    }

    @Test
    public void testGetStdDEV() {
        List<BigDecimal> value = new ArrayList<>();
        value.add(new BigDecimal(53));
        value.add(new BigDecimal(61));
        value.add(new BigDecimal(49));
        value.add(new BigDecimal(67));
        value.add(new BigDecimal(55));
        value.add(new BigDecimal(63));
        System.out.println(getStdDEV(value));
    }

    private double getAvg(List<BigDecimal> values) {
        BigDecimal sum = new BigDecimal(0.0f);
        for (BigDecimal v : values) {
            sum = sum.add(v);
        }
        BigDecimal count = new BigDecimal(values.size());
        BigDecimal avg = sum.divide(count, BigDecimal.ROUND_HALF_UP);
        return avg.doubleValue();
    }

    private double getStdDEV(List<BigDecimal> values) {
        BigDecimal sum = new BigDecimal(0.0f);
        for (BigDecimal v : values) {
            sum = sum.add(v);
        }
        BigDecimal count = new BigDecimal(values.size());
        BigDecimal avg = sum.divide(count, BigDecimal.ROUND_HALF_UP);
        //方差
        BigDecimal variance = new BigDecimal(0);
        for (BigDecimal v : values) {
            variance = variance.add(new BigDecimal(Math.pow(Math.abs(v.subtract(avg).doubleValue()), 2)));
        }
        //求方差 如果样本数据很大，可以除以n-1
//        variance = variance.divide(count.subtract(new BigDecimal(1)), BigDecimal.ROUND_HALF_UP);
        variance = variance.divide(count, BigDecimal.ROUND_HALF_UP);
        //求平方根返回标准差
        return Math.sqrt(variance.doubleValue());
    }
}
