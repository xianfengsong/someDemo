package jvm.gc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmsTester {

    private static List<DumbObj> liveObjs = new ArrayList<>(5);

    /**
     * -Xms200m -Xmn100m -Xmx210m -verbose:gc -XX:MetaspaceSize=10m
     * -XX:+UseConcMarkSweepGC -Xloggc:/tmp/gc.log -XX:+PrintGCCause -XX:+PrintGCTimeStamps
     * -XX:+PrintGCDetails -XX:CMSScheduleRemarkEdenPenetration=50 -XX:CMSScheduleRemarkEdenSizeThreshold=2
     * -XX:CMSMaxAbortablePrecleanTime=5000 -XX:+PrintCMSInitiationStatistics -XX:+UseCMSInitiatingOccupancyOnly
     * -XX:CMSInitiatingOccupancyFraction=50
     * -XX:PrintCMSStatistics=2
     */
    public static void main(String[] args) throws InterruptedException {
        // why keeping major gc
        for (int i = 0; i < 25; i++) {

            DumbObj dumb = new DumbObj(1, null);
            if (liveObjs.size() < 5) {
                liveObjs.add(new DumbObj(1, dumb));
            } else {
                dumb.setNext(liveObjs.get(i % 5));
            }
        }
        TimeUnit.SECONDS.sleep(20);
    }

}



