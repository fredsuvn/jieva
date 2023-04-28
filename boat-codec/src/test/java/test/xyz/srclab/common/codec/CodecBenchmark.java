//package test.xyz.srclab.common.codec;
//
//import org.apache.commons.codec.binary.Hex;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//import org.testng.Assert;
//import xyz.srclab.common.lang.Chars;
//import xyz.srclab.common.lang.Defaults;
//import xyz.srclab.common.test.TestLogger;
//
//import java.util.Base64;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author sunqian
// */
//@BenchmarkMode(Mode.Throughput)
//@Warmup(iterations = 3, time = 30)
//@Measurement(iterations = 3, time = 30)
//@Threads(8)
//@Fork(1)
//@State(value = Scope.Benchmark)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//public class CodecBenchmark {
//
//    private static final TestLogger logger = TestLogger.DEFAULT;
//
//    private String plainString = "好好学习天天向上！好好学习天天向上！好好学习天天向上！好好学习天天向上！";
//    private String base64String;
//    private String hexString;
//
//    @Setup
//    public void init() {
//        base64String = Base64.getEncoder().encodeToString(plainString.getBytes(Defaults.charset()));
//        hexString = Hex.encodeHexString(plainString.getBytes(Defaults.charset()));
//        logger.log("base64String: {}", base64String);
//        logger.log("hexString: {}", hexString);
//    }
//
//    @Benchmark
//    public void testJdkBase64() {
//        byte[] bytes = Base64.getDecoder().decode(base64String);
//        String decode = Chars.toChars(bytes);
//        Assert.assertEquals(decode, plainString);
//    }
//
//    @Benchmark
//    public void testApacheBase64() {
//        byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64String);
//        String decode = Chars.toChars(bytes);
//        Assert.assertEquals(decode, plainString);
//    }
//
//    @Benchmark
//    public void testBouncycastleBase642() {
//        byte[] bytes = org.bouncycastle.util.encoders.Base64.decode(base64String);
//        String decode = Chars.toChars(bytes);
//        Assert.assertEquals(decode, plainString);
//    }
//
//    @Benchmark
//    public void testApacheHex() throws Exception {
//        byte[] bytes = Hex.decodeHex(hexString);
//        String decode = Chars.toChars(bytes);
//        Assert.assertEquals(decode, plainString);
//    }
//
//    @Benchmark
//    public void testBouncycastleHex() {
//        byte[] bytes = org.bouncycastle.util.encoders.Hex.decode(hexString);
//        String decode = Chars.toChars(bytes);
//        Assert.assertEquals(decode, plainString);
//    }
//
//    /*
//     * Benchmark                                Mode  Cnt     Score       Error   Units
//     * CodecBenchmark.testApacheBase64         thrpt    3  1101.294 ±   370.320  ops/ms
//     * CodecBenchmark.testApacheHex            thrpt    3  6864.776 ±  7657.808  ops/ms
//     * CodecBenchmark.testBouncycastleBase642  thrpt    3  6676.349 ±  9807.417  ops/ms
//     * CodecBenchmark.testBouncycastleHex      thrpt    3  6137.142 ±  4013.271  ops/ms
//     * CodecBenchmark.testJdkBase64            thrpt    3  7168.556 ± 11451.995  ops/ms
//     */
//    public static void main(String[] args) throws Exception {
//        Options options = new OptionsBuilder().include(CodecBenchmark.class.getSimpleName()).build();
//        new Runner(options).run();
//    }
//}
