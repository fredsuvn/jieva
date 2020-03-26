package test.xyz.srclab.common.model;

import java.util.List;

public class BoundModel {

    public List<? super Integer> bound1(List<? extends Integer> list) {
        return null;
    }

    public <T extends Number> List<? super T> bound2(List<? extends T> list) {
        return null;
    }

    public <T extends Number> T bound3(T t) {
        return null;
    }
}
