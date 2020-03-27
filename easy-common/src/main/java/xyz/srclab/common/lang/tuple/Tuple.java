package xyz.srclab.common.lang.tuple;

public interface Tuple<A, B, C> {

    static <A, B, C> Tuple<A, B, C> of(A first, B second, C third) {
        return new Tuple<A, B, C>() {
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
