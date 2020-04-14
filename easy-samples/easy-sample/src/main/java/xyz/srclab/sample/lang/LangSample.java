package xyz.srclab.sample.lang;

import xyz.srclab.common.lang.Computed;
import xyz.srclab.common.lang.Ref;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.lang.tuple.Triple;

import java.util.List;

public class LangSample {

    public static void main(String[] args) {
        Computed<String> computed = Computed.with(() -> "complex build string");
        System.out.println(computed.get());

        Ref<String> ref = Ref.with("123");
        System.out.println(ref.get());

        Pair<String, Integer> pair = Pair.of("0", 1);
        System.out.println(pair.get0());
        System.out.println(pair.get1());

        Triple<String, Integer, Long> triple = Triple.of("0", 1, 2L);
        System.out.println(triple.get0());
        System.out.println(triple.get1());
        System.out.println(triple.get2());

        TypeRef<List<String>> listTypeRef = new TypeRef<List<String>>() {};
        System.out.println(listTypeRef.getType());
    }
}
