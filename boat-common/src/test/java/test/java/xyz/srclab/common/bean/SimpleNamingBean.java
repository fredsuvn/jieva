package test.java.xyz.srclab.common.bean;

import java.util.List;

/**
 * @author sunqian
 */
public class SimpleNamingBean {

    private String p1;
    private int p2;
    private List<String> p3;

    public String p1() {
        return p1;
    }

    public void p1(String p1) {
        this.p1 = p1;
    }

    public int p2() {
        return p2;
    }

    public void p2(int p2) {
        this.p2 = p2;
    }

    public List<String> p3() {
        return p3;
    }

    public void p3(List<String> p3) {
        this.p3 = p3;
    }
}
