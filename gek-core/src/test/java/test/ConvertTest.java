package test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Gek;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.mapper.JieMapper;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ConvertTest {

    @Test
    public void testConvert() {
        long now = System.currentTimeMillis();
        Assert.assertEquals(
            Instant.ofEpochMilli(now),
            Gek.convert(new Date(now), Instant.class)
        );
        Instant instant = Instant.ofEpochMilli(now);
        Assert.assertSame(
            instant,
            Gek.convert(instant, Instant.class)
        );
        Assert.assertEquals(
            Arrays.asList("1", "2", "3"),
            Gek.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
                }.getType(),
                new TypeRef<List<String>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Gek.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Gek.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Gek.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? extends String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Gek.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Gek.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? extends Integer>>() {
                }.getType()
            )
        );
        List<String> strList = Arrays.asList("1", "2", "3");
        Assert.assertSame(
            strList,
            Gek.convertType(
                strList, new TypeRef<List<? super CharSequence>>() {
                }.getType(),
                new TypeRef<List<? super String>>() {
                }.getType()
            )
        );
        Assert.assertNotSame(
            strList,
            Gek.convertType(
                strList, new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super CharSequence>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            strList,
            Gek.convertType(
                strList, new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super CharSequence>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            strList,
            Gek.convert(
                strList, List.class
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Gek.convert(
                strList,
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(new T1("1"), new T1("2")),
            Gek.convertType(
                Arrays.asList(new T2("1"), new T2("2")),
                new TypeRef<List<T2>>() {
                }.getType(),
                new TypeRef<List<? super T1>>() {
                }.getType()
            )
        );
        Assert.assertNotEquals(
            Arrays.asList(new T1("1"), new T1("2")),
            Gek.convertType(
                Arrays.asList(new T2("1"), new T2("2")),
                new TypeRef<List<T2>>() {
                }.getType(),
                new TypeRef<List<? extends T1>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            E1.E1,
            Gek.convert(
                "E1",
                E1.class
            )
        );
        Assert.assertEquals(
            "E2",
            Gek.convert(
                E1.E2,
                String.class
            )
        );
        Assert.assertEquals(
            E2.E1,
            Gek.convert(
                E1.E1,
                E2.class
            )
        );
        Assert.assertEquals(
            E1.E2,
            Gek.convert(
                E2.E2,
                E1.class
            )
        );
    }

    @Test
    public void testConvertBytes() {
        byte[] low = {1, 2, 3};
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2, 3});
        JieMapper converter = JieMapper.defaultMapper();
        JieMapper newConverter = converter.withOptions(
            converter.getOptions().replaceReusePolicy(JieMapper.Options.NO_REUSE));
        Assert.assertEquals(
            converter.map(low, byte[].class),
            low
        );
        Assert.assertEquals(
            converter.map(low, ByteBuffer.class),
            buffer.slice()
        );
        Assert.assertEquals(
            converter.map(buffer, byte[].class),
            low
        );
        Assert.assertEquals(
            converter.map(buffer, ByteBuffer.class),
            buffer.slice()
        );
        Assert.assertSame(
            converter.map(low, byte[].class),
            low
        );
        Assert.assertEquals(
            newConverter.map(low, byte[].class),
            low
        );
        Assert.assertNotSame(
            newConverter.map(low, byte[].class),
            low
        );
        Assert.assertNotSame(
            converter.map(buffer, ByteBuffer.class),
            buffer.slice()
        );
    }

    @Test
    public void testConvertHandler() {
        Object x = new Object();
        JieMapper.Handler handler = new JieMapper.Handler() {
            @Override
            public @Nullable Object map(
                @Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
                return x;
            }
        };
        JieMapper converter = JieMapper.defaultMapper().insertFirstMiddleHandler(handler);
        Assert.assertSame(
            x,
            converter.map(
                "123",
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        JieMapper converter2 = converter.withOptions(converter.getOptions());
        Assert.assertSame(converter, converter2);
        converter2 = converter.withOptions(converter.getOptions()
            .replaceReusePolicy(converter.getOptions().reusePolicy()));
        Assert.assertSame(converter, converter2);
        int reusePolicy = converter.getOptions().reusePolicy();
        converter2 = converter.withOptions(converter.getOptions()
            .replaceReusePolicy(reusePolicy + 1));
        Assert.assertNotSame(converter, converter2);
        converter2 = converter.withOptions(converter.getOptions()
            .replaceReusePolicy(reusePolicy));
        Assert.assertSame(converter, converter2);
    }

    @Test
    public void testConvertAsHandler() {
        JieMapper.Handler handler = JieMapper.defaultMapper()
            .insertFirstMiddleHandler(new JieMapper.Handler() {
                @Override
                public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
                    if (Objects.equals(source, "1")) {
                        return "2";
                    }
                    if (Objects.equals(source, "2")) {
                        return "1";
                    }
                    if (Objects.equals(source, "3")) {
                        return null;
                    }
                    return Flag.BREAK;
                }
            })
            .asHandler();
        Assert.assertEquals(
            handler.map("1", String.class, Integer.class, JieMapper.defaultMapper()), "2");
        Assert.assertEquals(
            handler.map("2", String.class, Integer.class, JieMapper.defaultMapper()), "1");
        Assert.assertEquals(
            handler.map("3", String.class, Integer.class, JieMapper.defaultMapper()), 3);
        Assert.assertEquals(
            handler.map("4", String.class, Integer.class, JieMapper.defaultMapper()), Flag.BREAK);
    }

    public enum E1 {
        E1, E2, E3,
        ;
    }

    public enum E2 {
        E1, E2, E3,
        ;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Data
    public static class T1 {
        private String value;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class T2 extends T1 {

        public T2() {
        }

        public T2(String value) {
            super(value);
        }
    }
}
