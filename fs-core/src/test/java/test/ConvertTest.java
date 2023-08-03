package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.FsConvert;
import xyz.srclab.common.reflect.TypeRef;

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
            FsConvert.convert(new Date(now), Instant.class)
        );
        Instant instant = Instant.ofEpochMilli(now);
        Assert.assertSame(
            instant,
            FsConvert.convert(instant, Instant.class)
        );
        Assert.assertEquals(
            Arrays.asList("1", "2", "3"),
            FsConvert.convertByType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
                }.getType(),
                new TypeRef<List<String>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            FsConvert.convertByType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            FsConvert.convertByType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            FsConvert.convertByType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? extends String>>() {
                }.getType(),
                new TypeRef<List<Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            FsConvert.convertByType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super Integer>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            Arrays.asList(1, 2, 3),
            FsConvert.convertByType(
                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? extends Integer>>() {
                }.getType()
            )
        );
        List<String> strList = Arrays.asList("1", "2", "3");
        Assert.assertSame(
            strList,
            FsConvert.convertByType(
                strList, new TypeRef<List<? super CharSequence>>() {
                }.getType(),
                new TypeRef<List<? super String>>() {
                }.getType()
            )
        );
        Assert.assertNotSame(
            strList,
            FsConvert.convertByType(
                strList, new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super CharSequence>>() {
                }.getType()
            )
        );
        Assert.assertEquals(
            strList,
            FsConvert.convertByType(
                strList, new TypeRef<List<? super String>>() {
                }.getType(),
                new TypeRef<List<? super CharSequence>>() {
                }.getType()
            )
        );
    }
}
