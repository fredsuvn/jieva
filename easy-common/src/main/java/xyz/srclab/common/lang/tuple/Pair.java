package xyz.srclab.common.lang.tuple;

public interface Pair<A, B> {

    static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<A, B>() {
            @Override
            public A get0() {
                return first;
            }

            @Override
            public B get1() {
                return second;
            }
        };
    }

    A get0();

    B get1();
}
