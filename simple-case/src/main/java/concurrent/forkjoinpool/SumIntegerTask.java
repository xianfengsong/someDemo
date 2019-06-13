package concurrent.forkjoinpool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * author Xianfeng <br/>
 * date 19-6-13 下午3:06 <br/>
 * Desc:
 * 对整形数组求和，超过4个，分散成子任务，有返回值
 */
public class SumIntegerTask extends RecursiveTask<Integer> {

    private static final int THRESHOLD = 4;
    private int[] arr;

    public SumIntegerTask(int[] arr) {
        this.arr = arr;
    }

    @Override
    protected Integer compute() {
        if (arr.length > THRESHOLD) {
            //将子任务结果求和
            return ForkJoinTask.invokeAll(createSubtasks())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        } else {
            return processing(arr);
        }
    }

    private List<SumIntegerTask> createSubtasks() {
        List<SumIntegerTask> subtasks = new ArrayList<>();
        int s = 0, e = THRESHOLD;
        while (s < e && e <= arr.length) {
            subtasks.add(new SumIntegerTask(Arrays.copyOfRange(arr, s, e)));
            s = e;
            e += THRESHOLD;
        }
        if (s < arr.length) {
            subtasks.add(new SumIntegerTask(Arrays.copyOfRange(arr, s, arr.length)));
        }
        return subtasks;
    }

    private Integer processing(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }
}
