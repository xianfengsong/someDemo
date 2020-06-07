package jvm.gc;

public class CmsTester {

    private static Byte[] live = new Byte[1];

    private static void gc() {
        System.gc();
    }

    private static void sleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
        int i = 0;
        while (i++ < 32) {
            live = getM(4);
        }
        sleep(10000L);
        System.gc();

//        int i=0;
//        while(i++<10){
//            live = getM(8);
//            sleep();
//        }
//        System.exit(0);
    }

}
