package test.java.xyz.srclab.common.bean;

import java.util.List;

/**
 * @author sunqian
 */
public class GenericBeanA extends GenericClass<String, List<String>>
        implements GenericInterface<Integer, List<Integer>> {

    private Integer i1;
    private List<Integer> i2;

    @Override
    public Integer getI1() {
        return i1;
    }

    @Override
    public void setI1(Integer i1) {
        this.i1 = i1;
    }

    @Override
    public List<Integer> getI2() {
        return i2;
    }

    @Override
    public void setI2(List<Integer> i2) {
        this.i2 = i2;
    }
}
