package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.fsgik.common.reflect.FsInvoker;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Threads(7)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InvokeBenchmark {

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

    /**
     * VM version: JDK 1.8.0_301, Java HotSpot(TM) 64-Bit Server VM, 25.301-b09:
     * <pre>
     * Benchmark                         Mode  Cnt      Score       Error   Units
     * InvokeBenchmark.reflectMember    thrpt    3  66889.576 ± 39813.556  ops/ms
     * InvokeBenchmark.reflectStatic    thrpt    3  69176.730 ± 75922.535  ops/ms
     * InvokeBenchmark.unreflectMember  thrpt    3  65625.810 ±  8991.974  ops/ms
     * InvokeBenchmark.unreflectStatic  thrpt    3  63415.634 ± 28069.864  ops/ms
     * </pre>
     * VM version: JDK 11, OpenJDK 64-Bit Server VM, 11+28:
     * <pre>
     * Benchmark                         Mode  Cnt       Score        Error   Units
     * InvokeBenchmark.reflectMember    thrpt    3  198214.303 ± 205024.805  ops/ms
     * InvokeBenchmark.reflectStatic    thrpt    3   69666.296 ±  24749.062  ops/ms
     * InvokeBenchmark.unreflectMember  thrpt    3   51888.739 ±  40284.824  ops/ms
     * InvokeBenchmark.unreflectStatic  thrpt    3   59570.344 ±  20049.375  ops/ms
     * </pre>
     * VM version: JDK 17.0.7, Java HotSpot(TM) 64-Bit Server VM, 17.0.7+8-LTS-224:
     * <pre>
     * Benchmark                         Mode  Cnt      Score        Error   Units
     * InvokeBenchmark.reflectMember    thrpt    3  66170.603 ±  22149.571  ops/ms
     * InvokeBenchmark.reflectStatic    thrpt    3  68658.280 ± 126528.975  ops/ms
     * InvokeBenchmark.unreflectMember  thrpt    3  61138.724 ±  44769.711  ops/ms
     * InvokeBenchmark.unreflectStatic  thrpt    3  61561.131 ±  18251.143  ops/ms
     * </pre>
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(InvokeBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
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
