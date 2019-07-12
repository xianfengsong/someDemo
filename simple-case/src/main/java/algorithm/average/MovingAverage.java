package algorithm.average;

import java.util.List;

/**
 * author Xianfeng <br/>
 * date 19-7-9 上午10:50 <br/>
 * Desc: 移动平均数算法,计算平均价格
 */
public interface MovingAverage {

    /**
     * 根据几天的价格计算平均值
     *
     * @param priceList 价格
     * @param days 天数
     * @return 平均价
     */
    double getAverage(List<Long> priceList, Integer days);

    /**
     * 根据最新的价格，更新平均价
     *
     * @param average 原平均价
     * @param oldPriceList 之间days天的价格列表
     * @param newPrice 新一天的价格
     * @param days 平均价计算天数
     * @return 新平均价
     */
    double refreshAverage(double average, List<Long> oldPriceList, Long newPrice, Integer days);
}
