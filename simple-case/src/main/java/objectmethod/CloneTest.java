package objectmethod;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CloneTest {
    @Test
    public void cloneEqual() throws CloneNotSupportedException {
        Myobj source = new Myobj();
        source.name = "x";
        Myobj child = new Myobj();
        child.name = "xx";
        child.children = new ArrayList<>();
        source.children = new ArrayList<>();
        source.children.add(child);
        Myobj copy = source.getCopy();
        System.out.println(copy == source);
        System.out.println(copy.getClass() == source.getClass());
        System.out.println(copy.equals(source));
        System.out.println(String.format("source:%s\r\ncopy:%s", source, copy));

        source.children.add(child);
        Assert.assertNotEquals("source改变，copy也改变", 1, copy.children.size());
        System.out.println(String.format("source:%s\r\ncopy:%s", source, copy));

    }

    class Myobj implements Cloneable {
        String name;
        List<Myobj> children;

        @Override
        public String toString() {
            return "Myobj{" +
                    "name='" + name + '\'' +
                    ", children=" +
                    (children == null ? "NULL" : children.stream()
                            .map(Myobj::toString)
                            .collect(Collectors.joining(",")))
                    + '}';
        }

        public Myobj getCopy() throws CloneNotSupportedException {
            return (Myobj) super.clone();
        }
    }
}
