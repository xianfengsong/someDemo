package jvm.gc;

public class DumbObj {
    private DumbObj next;
    private Byte[] data;

    public DumbObj(int sizeM, DumbObj next) {
        this.data = getM(sizeM);
        this.next = next;
    }

    private Byte[] getM(int m) {
        return new Byte[1024 * 1024 * m];
    }

    public Byte[] getData() {
        return data;
    }

    public void setData(Byte[] data) {
        this.data = data;
    }


    public DumbObj getNext() {
        return next;
    }

    public void setNext(DumbObj next) {
        this.next = next;
    }
}
