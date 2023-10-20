package benchmark.jmh;

import org.openjdk.jmh.annotations.*;
import xyz.fsgek.common.reflect.FsInvoker;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InvokeJmh {

    private static final Method helloStatic;
    private static final Method helloMember;
    private static final FsInvoker reflectStaticInvoker;
    private static final FsInvoker reflectMemberInvoker;
    private static final FsInvoker unreflectStaticInvoker;
    private static final FsInvoker unreflectMemberInvoker;
    private static final TT tt = new TT();

    static {
        try {
            helloStatic = TT.class.getDeclaredMethod("helloStatic", String.class, String.class);
            helloMember = TT.class.getDeclaredMethod("helloMember", String.class, String.class);
            reflectStaticInvoker = FsInvoker.reflectMethod(helloStatic);
            reflectMemberInvoker = FsInvoker.reflectMethod(helloMember);
            unreflectStaticInvoker = FsInvoker.unreflectMethod(helloStatic);
            unreflectMemberInvoker = FsInvoker.unreflectMethod(helloMember);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Setup
    public void init() {
    }

    @Benchmark
    public void reflectStatic() {
        reflectStaticInvoker.invoke(null, "a", "b");
    }

    @Benchmark
    public void reflectMember() {
        reflectMemberInvoker.invoke(tt, "a", "b");
    }

    @Benchmark
    public void unreflectStatic() {
        unreflectStaticInvoker.invoke(null, "a", "b");
    }

    @Benchmark
    public void unreflectMember() {
        unreflectMemberInvoker.invoke(tt, "a", "b");
    }

    public static class TT {

        public static String helloStatic(String a, String b) {
            return "helloStatic: " + a + ", " + b;
        }

        public String helloMember(String a, String b) {
            return "helloMember: " + a + ", " + b;
        }
    }
}
