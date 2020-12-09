package test.java.xyz.srclab.common.bean;

/**
 * @author sunqian
 */
public interface GenericInterface<I1, I2> {

    I1 getI1();

    void setI1(I1 i1);

    I2 getI2();

    void setI2(I2 i2);

    default I1 getI11() {
        return null;
    }

    default void setI11(I1 i11) {
    }

    default I2 getI22() {
        return null;
    }

    default void setI22(I2 i22) {
    }
}
