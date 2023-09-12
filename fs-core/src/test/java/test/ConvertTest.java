package test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
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
            Fs.convert(new Date(now), Instant.class)
        );
        Instant instant = Instant.ofEpochMilli(now);
        Assert.assertSame(
            instant,
            Fs.convert(instant, Instant.class)
        );
        Assert.assertEquals(
            Arrays.asList("1", "2", "3"),
            Fs.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
                }.getType(),
                new TypeRef<List<String>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Fs.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Fs.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Fs.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? extends String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Fs.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Fs.convertType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? extends Integer>>() {
                }.getType()
            )
        );
        List<String> strList = Arrays.asList("1", "2", "3");
        Assert.assertSame(
            strList,
            Fs.convertType(
                strList, new TypeRef<List<? super CharSequence>>() {
                }.getType(),
                new TypeRef<List<? super String>>() {
                }.getType()
            )
        );
        Assert.assertNotSame(
            strList,
            Fs.convertType(
                strList, new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super CharSequence>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            strList,
            Fs.convertType(
                strList, new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super CharSequence>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            strList,
            Fs.convert(
                strList, List.class
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            Fs.convert(
                strList,
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(new T1("1"), new T1("2")),
            Fs.convertType(
                Arrays.asList(new T2("1"), new T2("2")),
                new TypeRef<List<T2>>() {
                }.getType(),
                new TypeRef<List<? super T1>>() {
                }.getType()
            )
        );
        Assert.assertNotEquals(
            Arrays.asList(new T1("1"), new T1("2")),
            Fs.convertType(
                Arrays.asList(new T2("1"), new T2("2")),
                new TypeRef<List<T2>>() {
                }.getType(),
                new TypeRef<List<? extends T1>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            E1.E1,
            Fs.convert(
                "E1",
                E1.class
            )
        );
        Assert.assertEquals(
            "E2",
            Fs.convert(
                E1.E2,
                String.class
            )
        );
        Assert.assertEquals(
            E2.E1,
            Fs.convert(
                E1.E1,
                E2.class
            )
        );
        Assert.assertEquals(
            E1.E2,
            Fs.convert(
                E2.E2,
                E1.class
            )
        );
    }

    @Test
    public void testConvertHandler() {
        Object x = new Object();
        FsConverter.Handler handler = new FsConverter.Handler() {
            @Override
            public @Nullable Object convert(
                @Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
                return x;
            }
        };
        FsConverter converter = FsConverter.defaultConverter().withMiddleHandler(handler);
        Assert.assertSame(
            x,
            converter.convert(
                "123",
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        FsConverter converter2 = converter.withOptions(converter.getOptions());
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
        FsConverter.Handler handler = FsConverter.defaultConverter()
            .withMiddleHandler(new FsConverter.Handler() {
                @Override
                public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
                    if (Objects.equals(source, "1")) {
                        return "2";
                    }
                    if (Objects.equals(source, "2")) {
                        return "1";
                    }
                    if (Objects.equals(source, "3")) {
                        return Fs.CONTINUE;
                    }
                    return Fs.BREAK;
                }
            })
            .asHandler();
        Assert.assertEquals(
            handler.convert("1", String.class, Integer.class, FsConverter.defaultConverter()), "2");
        Assert.assertEquals(
            handler.convert("2", String.class, Integer.class, FsConverter.defaultConverter()), "1");
        Assert.assertEquals(
            handler.convert("3", String.class, Integer.class, FsConverter.defaultConverter()), 3);
        Assert.assertEquals(
            handler.convert("4", String.class, Integer.class, FsConverter.defaultConverter()), Fs.BREAK);
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

    public enum E1 {
        E1, E2, E3,
        ;
    }

    public enum E2 {
        E1, E2, E3,
        ;
    }
}
