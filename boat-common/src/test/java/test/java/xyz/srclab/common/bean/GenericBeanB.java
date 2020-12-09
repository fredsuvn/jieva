package test.java.xyz.srclab.common.bean;

import java.util.List;

/**
 * @author sunqian
 */
public class GenericBeanB extends GenericClass<Integer, List<Integer>>
        implements GenericInterface<String, List<String>> {

    private String i1;
    private List<String> i2;

    @Override
    public String getI1() {
        return i1;
    }

    @Override
    public void setI1(String i1) {
        this.i1 = i1;
    }

    @Override
    public List<String> getI2() {
        return i2;
    }

    @Override
    public void setI2(List<String> i2) {
        this.i2 = i2;
    }
}
