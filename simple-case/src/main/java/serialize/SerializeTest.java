package serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 2020/12/23 下午7:19 <br/>
 * Desc:
 */
public class SerializeTest {

    String path = "/tmp/serialize";

    @Test
    public void serializeAndDeserialize() {
        Demo.hiddenStatic = -1;
        Demo d = new Demo();
        d.name = "name";
        d.count = 1;
        d.hiddenTrans = "transient";

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(d);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Demo copy = (Demo) in.readObject();
            System.out.println(copy);
            Assert.assertEquals(copy.name, d.name);
            Assert.assertNull(copy.hiddenTrans);
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
        }
    }

}
