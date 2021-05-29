package sample.java.xyz.srclab.core.convert;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.Converts;
import xyz.srclab.common.convert.FastConvertHandler;
import xyz.srclab.common.convert.FastConverter;
import xyz.srclab.common.test.TestLogger;

import java.util.Arrays;

public class ConvertSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testConvert() {
        String s = Converts.convert(123, String.class);
        //123
        logger.log("s: {}", s);

        A a = new A();
        a.setP1("1");
        a.setP2("2");
        B b = Converts.convert(a, B.class);
        //1
        logger.log("b1: {}", b.getP1());
        //2
        logger.log("b1: {}", b.getP2());

        FastConverter fastConverter = FastConverter.newFastConverter(
            Arrays.asList(new IntToStringConvertHandler(), new NumberToStringConvertHandler()));
        //I123
        logger.log(fastConverter.convert(123, String.class));
        //N123
        logger.log(fastConverter.convert(123L, String.class));
    }

    public static class A {
        private String p1;
        private String p2;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public String getP2() {
            return p2;
        }

        public void setP2(String p2) {
            this.p2 = p2;
        }
    }

    public static class B {
        private int p1;
        private int p2;

        public int getP1() {
            return p1;
        }

        public void setP1(int p1) {
            this.p1 = p1;
        }

        public int getP2() {
            return p2;
        }

        public void setP2(int p2) {
            this.p2 = p2;
        }
    }

    private static class IntToStringConvertHandler implements FastConvertHandler<Integer, String> {

        @NotNull
        @Override
        public Class<?> fromType() {
            return Integer.class;
        }

        @NotNull
        @Override
        public Class<?> toType() {
            return String.class;
        }

        @NotNull
        @Override
        public String convert(@NotNull Integer from) {
            return "I" + from;
        }
    }

    private static class NumberToStringConvertHandler implements FastConvertHandler<Number, String> {

        @NotNull
        @Override
        public Class<?> fromType() {
            return Number.class;
        }

        @NotNull
        @Override
        public Class<?> toType() {
            return String.class;
        }

        @NotNull
        @Override
        public String convert(@NotNull Number from) {
            return "N" + from;
        }
    }
}
