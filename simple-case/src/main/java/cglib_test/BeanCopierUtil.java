package main.java.cglib_test;

import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * 使用cglib的
 */
public class BeanCopierUtil {
    private static BeanCopier ComputerCopier = BeanCopier.create(Phone.class, Computer.class, false);

    class Phone{
        int ram;
        int rom;
        int pixel;
        String vendor;

        public int getRam() {
            return ram;
        }

        public void setRam(int ram) {
            this.ram = ram;
        }

        public int getRom() {
            return rom;
        }

        public void setRom(int rom) {
            this.rom = rom;
        }

        public int getPixel() {
            return pixel;
        }

        public void setPixel(int pixel) {
            this.pixel = pixel;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        @Override
        public String toString() {
            return "Phone{" +
                    "ram=" + ram +
                    ", rom=" + rom +
                    ", pixel=" + pixel +
                    ", vendor='" + vendor + '\'' +
                    "'";
        }
    }
    class Computer{
        public int ram;
        int rom;
        String vendor;
        String keyboard;
        String price;

        public int getRam() {
            return ram;
        }

        public void setRam(int ram) {
            this.ram = ram;
        }

        public int getRom() {
            return rom;
        }

        public void setRom(int rom) {
            this.rom = rom;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getKeyboard() {
            return keyboard;
        }

        public void setKeyboard(String keyboard) {
            this.keyboard = keyboard;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "Computer{" +
                    "ram=" + ram +
                    ", rom=" + rom +
                    ", vendor='" + vendor + '\'' +
                    ", keyboard='" + keyboard + '\'' +
                    ", price='" + price + '\'' +
                    '}';
        }
    }

    public static void main(String [] args){
        BeanCopierUtil b=new BeanCopierUtil();
        Phone phone=b.new Phone();
        phone.pixel=800;
        phone.ram=2;
        phone.rom=64;
        phone.vendor=null;
        Computer c=b.new Computer();
        c.vendor="dell";
        c.price="$99";
        copyProperties(phone,c);
        System.out.println(c);
        System.out.println(phone);
        ComputerCopier.copy(phone,c,null);
        System.out.println(c);
    }

    private static final Map<String, BeanCopier> beanCopierCache = new ConcurrentHashMap<>();


    private static BeanCopier getBeanCopier(Class sourceClass, Class targetClass) {
        String beanKey = generateKey(sourceClass, targetClass);
        BeanCopier copier = null;
        if (!beanCopierCache.containsKey(beanKey)) {
            copier = BeanCopier.create(sourceClass, targetClass, false);
            beanCopierCache.put(beanKey, copier);
        } else {
            copier = beanCopierCache.get(beanKey);
        }
        return copier;
    }

    private static String generateKey(Class<?> class1, Class<?> class2) {
        return class1.toString() + class2.toString();
    }

    /**
     * 复制属性
     * @param source source
     * @param target target
     */
    public static void copyProperties(Object source, Object target) {
        BeanCopier copier = getBeanCopier(source.getClass(), target.getClass());
        copier.copy(source, target, null);
    }

    /**
     * 根据对象复制另一个对象的新实例
     * @param source source
     * @param targetClass targetClass
     * @param <T> T
     * @return T
     */
    public static <T> T getCopiedInstance(Object source, Class<T> targetClass) {
        T t = null;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(format("Create new instance of %s failed: %s", targetClass, e.getMessage()));
        }
        copyProperties(source, t);
        return t;
    }
}
