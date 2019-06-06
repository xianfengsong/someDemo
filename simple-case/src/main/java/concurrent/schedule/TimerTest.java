package concurrent.schedule;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-3 上午11:33 <br/>
 * Desc: 测试java提供的定时器
 */
public class TimerTest {

    /**
     * @return 下一分钟的第0秒时刻
     */
    Date getNextMinuteBegging() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.add(Calendar.MINUTE, 1);
        return now.getTime();
    }

    /**
     * 测试timer的行为
     * 1.单独线程执行任务
     * 2.从开始时间每个固定间隔就执行
     */
    @Test
    public void testTimer() throws ParseException, InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getId());
                    System.out.println(new Date() + "\they");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, getNextMinuteBegging(), 500L);
        System.out.println(Thread.currentThread().getId());
        Thread.sleep(500_000L);
    }

    /**
     * timer一个任务异常后，之后的不会再执行
     * 所以要在任务内部处理完异常
     */
    @Test
    public void testExceptionInTimer() throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId());
                System.out.println(new Date() + "\t hey！");
                throw new RuntimeException("3");
            }
        }, getNextMinuteBegging(), 500L);
        System.out.println(Thread.currentThread().getId());
        Thread.currentThread().join();
    }

}
