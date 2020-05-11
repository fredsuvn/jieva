package xyz.srclab.common.lang;

public interface Triple<A, B, C> {

    static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<A, B, C>() {
            @Override
            public A get0() {
                return first;
            }

            @Override
            public B get1() {
                return second;
            }

            @Override
            public C get2() {
                return third;
            }
        };
    }

    A get0();

    B get1();

    C get2();
}
