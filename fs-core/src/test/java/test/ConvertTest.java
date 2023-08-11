package test;

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
    }

    @Test
    public void testConvertHandler() {
        Object x = new Object();
        FsConverter.Handler handler = new FsConverter.Handler() {
            @Override
            public @Nullable Object convert(
                @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options, FsConverter converter) {
                return x;
            }
        };
        FsConverter converter = FsConverter.defaultConverter().withCommonHandler(handler);
        Assert.assertSame(
            x,
            converter.convert(
                "123",
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
    }
}
