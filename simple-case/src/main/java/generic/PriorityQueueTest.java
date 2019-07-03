package generic;

import java.util.Comparator;
import java.util.PriorityQueue;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-7-2 上午9:57 <br/>
 * Desc: 测试PriorityQueue的性质，内部使用了堆结构
 */
public class PriorityQueueTest {

    /**
     * 测试 PriorityQueue默认是小根堆，每个父节点都小于子节点
     */
    @Test
    public void defaultMinHeap() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.offer(6);
        queue.offer(4);
        queue.offer(8);
        queue.offer(2);
        Assert.assertEquals("2在第一位", 2, queue.peek().intValue());
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }

    /**
     * 测试 通过传入Comparator，控制PriorityQueue中父节点和子节点的排序方式，可以得到大根堆
     */
    @Test
    public void maxHeap() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        queue.offer(6);
        queue.offer(4);
        queue.offer(8);
        queue.offer(2);
        Assert.assertEquals("8在第一位", 8, queue.peek().intValue());
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }


    /**
     * 测试 使用PriorityQueue做小根堆，找到数组中第k大的元素
     */
    @Test
    public void solveTopK() {
        int[] numbers = new int[10000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = 10000 - i;
        }
        //第几大
        int k = 10;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(k);
        for (int number : numbers) {
            if (priorityQueue.size() < k || number > priorityQueue.peek()) {
                priorityQueue.offer(number);
            }
            if (priorityQueue.size() > k) {
                priorityQueue.poll();
            }
        }

        Assert.assertEquals("9991是第10大", 9991, priorityQueue.peek().intValue());
    }
}
