package algorithm.loadbalance;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * author Xianfeng <br/>
 * date 18-11-22 下午4:36 <br/>
 * Desc:
 */

public class ConsistentHashDemo {

    private final static String PORT = "8001";
    private final static long REQUEST_NUMBER = 10000;
    private String[] servers = new String[]{"10.11.211.221", "127.0.0.1", "10.219.129.3"};
    private TreeMap<Long, String> circle = new TreeMap<>();

    private static long convertIPFromString(String IpString) {
        String[] ipPart = IpString.split("\\.");
        Assert.isTrue(ipPart.length == 4);
        return Long.valueOf(ipPart[0]) << 24 | Long.valueOf(ipPart[1]) << 16
                | Long.valueOf(ipPart[2]) << 8 | Long.valueOf(ipPart[3]);
    }

    private void initCircle() {
        for (String server : servers) {
            server = convertIPFromString(server) + PORT;
            byte[] bKey = DigestUtils.md5(server);
            long key = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | (
                    (long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
            circle.put(key & Integer.MAX_VALUE, server);
        }
    }

    private void initCircleWithVirtualNode(int eachVirtualNodeSize) {
        for (String server : servers) {
            server = convertIPFromString(server) + PORT;
            for (int index = 0; index < eachVirtualNodeSize; index++) {
                byte[] bKey = DigestUtils.md5(server + index);
                long key = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | (
                        (long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
                circle.put(key & Integer.MAX_VALUE, server);
            }
        }
    }

    private void doRequest() {
        Random random = new Random(6);
        Map<String, Integer> slotSizeMap = new HashMap<>();
        for (long i = 0; i < REQUEST_NUMBER; i++) {
            Long request = (long) random.nextInt(Integer.MAX_VALUE / 10000);
            Long position = request * 10000 % Integer.MAX_VALUE;
            Map.Entry<Long, String> server = circle.ceilingEntry(position);
            if (server == null) {
                server = circle.firstEntry();
            }
            String slot = server.getValue();
            slotSizeMap.merge(slot, 1, (a, b) -> a + b);
        }
        slotSizeMap.keySet().forEach(e -> System.out.println(e + ":\t" + slotSizeMap.get(e)));
    }

    @Test
    public void test() {
        initCircle();
        doRequest();
    }

    @Test
    public void testVirtual() {
        initCircleWithVirtualNode(200);
        doRequest();
    }

    @Test
    public void testVirtualScale() {
        initCircleWithVirtualNode(200);
        doRequest();
        String newServer = convertIPFromString("10.232.52.219") + PORT;
        for (int index = 0; index < 200; index++) {
            byte[] bKey = DigestUtils.md5(newServer + index);
            long key = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | (
                    (long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
            circle.put(key & Integer.MAX_VALUE, newServer);
        }
        System.out.println("扩容后：");
        doRequest();
    }
}
