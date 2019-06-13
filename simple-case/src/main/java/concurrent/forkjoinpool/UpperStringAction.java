package concurrent.forkjoinpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * author Xianfeng <br/>
 * date 19-6-13 下午2:56 <br/>
 * Desc:
 * 把string转出大写的action,没有返回值
 * 超过4个字符，就拆分任务
 */
class UpperStringAction extends RecursiveAction {

    private static final int THRESHOLD = 4;
    private String workload = "";

    public UpperStringAction(String workload) {
        this.workload = workload;
    }


    @Override
    protected void compute() {
        //不能抛出受检查的异常
        if (workload == null) {
            throw new IllegalArgumentException("workload为空");
        }
        if (workload.length() > THRESHOLD) {
            ForkJoinTask.invokeAll(createSubTasks());
        } else {
            processing(workload);
        }

    }

    private List<UpperStringAction> createSubTasks() {
        List<UpperStringAction> subtasks = new ArrayList<>();
        int s = 0, e = THRESHOLD;
        while (s < e && e <= workload.length()) {
            String part = workload.substring(s, e);
            subtasks.add(new UpperStringAction(part));
            s = e;
            e += THRESHOLD;
        }
        if (s < workload.length()) {
            subtasks.add(new UpperStringAction(workload.substring(s, workload.length())));
        }
        return subtasks;
    }

    private void processing(String workload) {
        System.out.println(workload.toUpperCase());
//        Thread.dumpStack();
    }
}
