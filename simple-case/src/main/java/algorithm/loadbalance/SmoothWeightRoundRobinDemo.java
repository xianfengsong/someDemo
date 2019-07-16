package algorithm.loadbalance;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

/**
 * author Xianfeng <br/>
 * date 19-7-16 下午5:39 <br/>
 * Desc: nginx的平滑权重轮训算法
 * https://github.com/phusion/nginx/commit/27e94984486058d73157038f7950a0a36ecc6e35
 */
public class SmoothWeightRoundRobinDemo {

    public Node select(List<Node> nodeList) {
        if (CollectionUtils.isEmpty(nodeList)) {
            return null;
        }
        int totalWeight = 0;
        Node maxWeightNode = new Node("", 0, 0);

        for (Node node : nodeList) {
            //第一步 每个节点的当前权重=权重+当前权重
            node.currentWeight += node.weight;
            if (node.currentWeight > maxWeightNode.weight) {
                maxWeightNode = node;
            }
            totalWeight += node.weight;
        }
        //第二步 选择当前权重最大的节点返回，并更新当前权重减去 总权重 作为新当前权重
        maxWeightNode.currentWeight -= totalWeight;
        return maxWeightNode;
    }

    /**
     * 测试结果是否按照权重分布
     */
    @Test
    public void testWeight() {
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(new Node("A", 5, 0));
        nodeList.add(new Node("B", 2, 0));
        nodeList.add(new Node("C", 1, 0));

        int a = 0, b = 0, c = 0;
        for (int i = 0; i < 80000; i++) {
            Node selected = select(nodeList);
            if ("A".equals(selected.id)) {
                a++;
            }
            if ("B".equals(selected.id)) {
                b++;
            }
            if ("C".equals(selected.id)) {
                c++;
            }
        }
        System.out.println(String.format("分布结果 A:%d B:%d C:%d", a, b, c));
        Assert.assertEquals("刚好50000，好准啊", 50000, a);
    }

    /**
     * 测试结果是否平滑（即使是权重高的节点，也不会连续访问一个）
     * 并且连续访问次数不会超过这个节点的权重
     */
    @Test
    public void testSmooth() {
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(new Node("A", 5, 0));
        nodeList.add(new Node("B", 2, 0));
        nodeList.add(new Node("C", 1, 0));
        List<String> statics = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            Node selected = select(nodeList);
            statics.add(selected.id);
        }
        System.out.println("前十次选择节点顺序" + statics.subList(0, 10));
        //记录节点最多被访问了几次
        int repeat = 0, maxRepeat = 0;
        String lastNode = "", maxRepeatNode = "";
        for (String record : statics) {
            if (record.equals(lastNode)) {
                repeat++;
                if (repeat > maxRepeat) {
                    maxRepeat = repeat;
                    maxRepeatNode = record;
                }
            } else {
                repeat = 1;
            }
            lastNode = record;
        }
        System.out.println("最大重复选择同一节点的次数:" + maxRepeat + " 节点名：" + maxRepeatNode);
        Assert.assertTrue(maxRepeat < 5);

    }

    class Node {

        String id;
        Integer weight;
        Integer currentWeight;

        public Node() {
        }

        public Node(String id, Integer weight, Integer currentWeight) {
            this.id = id;
            this.weight = weight;
            this.currentWeight = currentWeight;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id='" + id + '\'' +
                    ", weight=" + weight +
                    ", currentWeight=" + currentWeight +
                    '}';
        }
    }
}
