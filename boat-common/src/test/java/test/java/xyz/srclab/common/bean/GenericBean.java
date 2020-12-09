package test.java.xyz.srclab.common.bean;

import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class GenericBean<T1, T2, T3>
        extends GenericClass<T1, List<? extends T2>>
        implements GenericInterface<T3, Map<? super T3, List<? extends T3>>> {

    @Override
    public T3 getI1() {
        return null;
    }

    @Override
    public void setI1(T3 t3) {
    }

    @Override
    public Map<? super T3, List<? extends T3>> getI2() {
        return null;
    }

    @Override
    public void setI2(Map<? super T3, List<? extends T3>> listMap) {
    }
}
