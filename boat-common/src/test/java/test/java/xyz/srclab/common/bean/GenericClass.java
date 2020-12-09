package test.java.xyz.srclab.common.bean;

/**
 * @author sunqian
 */
public class GenericClass<A1, A2> {

    private A1 a1;
    private A2 a2;

    public A1 getA1() {
        return a1;
    }

    public void setA1(A1 a1) {
        this.a1 = a1;
    }

    public A2 getA2() {
        return a2;
    }

    public void setA2(A2 a2) {
        this.a2 = a2;
    }
}
