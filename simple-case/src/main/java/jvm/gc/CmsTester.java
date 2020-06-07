package jvm.gc;

import java.util.concurrent.TimeUnit;

public class CmsTester {

    private static Byte[] live1;

    private static Byte[] getM(int m) {
        return new Byte[1024 * 1024 * m];
    }

    /**
     * -Xms256m
     * -Xmn128m
     * -Xmx256m
     * -XX:+UseConcMarkSweepGC
     * -Xloggc:/tmp/gc.log
     * -XX:+PrintGCTimeStamps
     * -XX:+PrintGCDetails
     * -XX:+PrintGCCause
     * -XX:CMSScheduleRemarkEdenPenetration=50
     * -XX:CMSScheduleRemarkEdenSizeThreshold=2
     * -XX:CMSMaxAbortablePrecleanTime=5000
     */
    public static void main(String[] args) throws InterruptedException {
        // why keeping major gc
        int i = 0;
        while (i++ < 20) {
            live1 = getM(4);
        }
        TimeUnit.SECONDS.sleep(20);
//        int i=0;
//        while(i++<10){
//            live = getM(8);
//            sleep();
//        }
//        System.exit(0);
    }

}
