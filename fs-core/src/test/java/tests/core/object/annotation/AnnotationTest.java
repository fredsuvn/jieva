package tests.core.object.annotation;

import internal.utils.Asserter;
import internal.utils.TestPrint;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.annotation.AnnotationDetail;
import space.sunqian.fs.object.annotation.AnnotationGroup;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.DatePatternDetail;
import space.sunqian.fs.object.annotation.DetailType;
import space.sunqian.fs.object.annotation.NumberPattern;
import space.sunqian.fs.object.annotation.NumberPatternDetail;
import space.sunqian.fs.object.annotation.SimpleAnnotationDetail;
import space.sunqian.fs.object.convert.ConvertOption;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationTest implements Asserter, TestPrint {

    @Test
    public void testDetail() throws Exception {
        DatePattern datePattern = D.class.getDeclaredField("a").getAnnotation(DatePattern.class);
        assertEquals("yyyy-MM-dd", datePattern.value());
        assertInstanceOf(DatePatternDetail.class, AnnotationDetail.newDetail(datePattern));
        NumberPattern numberPattern = D.class.getDeclaredField("b").getAnnotation(NumberPattern.class);
        assertEquals("#.0000", numberPattern.value());
        assertInstanceOf(NumberPatternDetail.class, AnnotationDetail.newDetail(numberPattern));
        XAnn xAnn = D.class.getDeclaredField("x").getAnnotation(XAnn.class);
        assertInstanceOf(SimpleAnnotationDetail.class, AnnotationDetail.newDetail(xAnn));
        YAnn yAnn = D.class.getDeclaredField("y").getAnnotation(YAnn.class);
        assertInstanceOf(YAnnDetail.class, AnnotationDetail.newDetail(yAnn));
        Nullable nullable = D.class.getDeclaredField("n").getAnnotation(Nullable.class);
        assertInstanceOf(SimpleAnnotationDetail.class, AnnotationDetail.newDetail(nullable));
    }

    @Test
    public void testAnnotationSet() throws Exception {
        Field fieldA = X.class.getDeclaredField("a");
        AnnotationGroup annotationGroupA = AnnotationGroup.from(fieldA);
        testAnnotations(annotationGroupA);
        testDetails(annotationGroupA);
        Field fieldB = X.class.getDeclaredField("b");
        AnnotationGroup annotationGroupB = AnnotationGroup.from(fieldB);
        testFieldB(annotationGroupB);
    }

    private void testAnnotations(AnnotationGroup annotationGroup) throws Exception {
        List<Annotation> annotations = annotationGroup.annotations();
        assertEquals(4, annotations.size());
        assertNotNull(annotations.stream().filter(a -> a instanceof NumberPattern).findFirst().orElse(null));
        assertNotNull(annotations.stream().filter(a -> a instanceof DatePattern).findFirst().orElse(null));
        assertNotNull(annotations.stream().filter(a -> a instanceof Nullable).findFirst().orElse(null));
        assertNotNull(annotations.stream().filter(a -> a instanceof AS).findFirst().orElse(null));
        NumberPattern numberPattern = annotationGroup.annotation(NumberPattern.class);
        assertEquals("#.0000", numberPattern.value());
        DatePattern datePattern = annotationGroup.annotation(DatePattern.class);
        assertEquals("yyyy-MM-dd", datePattern.value());
        assertEquals("", datePattern.zoneId());
        Nullable nullable = annotationGroup.annotation(Nullable.class);
        AS as = annotationGroup.annotation(AS.class);
        assertEquals(3, as.value().length);
        assertEquals("1", as.value()[0].value());
        assertEquals("2", as.value()[1].value());
        assertEquals("3", as.value()[2].value());
        assertNull(annotationGroup.annotation(Nonnull.class));
    }

    private void testDetails(AnnotationGroup annotationGroup) throws Exception {
        List<AnnotationDetail<?>> details = annotationGroup.details();
        assertEquals(4, details.size());
        assertNotNull(details.stream().filter(a -> a instanceof NumberPatternDetail).findFirst().orElse(null));
        assertNotNull(details.stream().filter(a -> a instanceof DatePatternDetail).findFirst().orElse(null));
        assertEquals(2, details.stream().filter(a -> a instanceof SimpleAnnotationDetail<?>).collect(Collectors.toList()).size());
        NumberPatternDetail numberPattern = annotationGroup.detail(NumberPatternDetail.class);
        assertSame(annotationGroup.annotation(NumberPattern.class), numberPattern.annotation());
        assertEquals("11.1122", numberPattern.formatter().format(11.11223344).toString());
        assertSame(ConvertOption.NUMBER_FORMATTER, numberPattern.option().key());
        assertSame(numberPattern.formatter(), numberPattern.option().value());
        DatePatternDetail datePattern = annotationGroup.detail(DatePatternDetail.class);
        assertSame(annotationGroup.annotation(DatePattern.class), datePattern.annotation());
        assertEquals(ZoneId.systemDefault(), datePattern.zoneId());
        Date date = new Date();
        assertEquals(
            new SimpleDateFormat("yyyy-MM-dd").format(date),
            datePattern.formatter().format(date)
        );
        assertSame(ConvertOption.DATE_FORMATTER, datePattern.option().key());
        assertSame(datePattern.formatter(), datePattern.option().value());
        SimpleAnnotationDetail<?> nullable = annotationGroup.detail(SimpleAnnotationDetail.class);
        assertSame(annotationGroup.annotation(Nullable.class), nullable.annotation());
        SimpleAnnotationDetail<AS> as = annotationGroup.detailFor(AS.class);
        assertSame(annotationGroup.annotation(AS.class), as.annotation());
        assertNull(annotationGroup.detailFor(Nonnull.class));
    }

    private void testFieldB(AnnotationGroup annotationGroup) throws Exception {
        NumberPatternDetail numberPattern = annotationGroup.detail(NumberPatternDetail.class);
        assertEquals(
            new DecimalFormat(numberPattern.annotation().value()).format(11.11223344),
            numberPattern.formatter().format(11.11223344).toString()
        );
        DatePatternDetail datePattern = annotationGroup.detail(DatePatternDetail.class);
        assertEquals(ZoneId.of("Asia/Shanghai"), datePattern.zoneId());
    }

    @Test
    public void testMultiAnnotationSet() throws Exception {
        Field fieldA = M.class.getDeclaredField("a");
        AnnotationGroup annotationGroupA = AnnotationGroup.from(fieldA);
        DatePattern pa = annotationGroupA.annotation(DatePattern.class);
        DatePatternDetail da = annotationGroupA.detail(DatePatternDetail.class);
        Field fieldB = M.class.getDeclaredField("b");
        AnnotationGroup annotationGroupB = AnnotationGroup.from(fieldB);
        DatePattern pb = annotationGroupB.annotation(DatePattern.class);
        DatePatternDetail db = annotationGroupB.detail(DatePatternDetail.class);
        NumberPattern nb = annotationGroupB.annotation(NumberPattern.class);
        NumberPatternDetail ndb = annotationGroupB.detail(NumberPatternDetail.class);
        AnnotationGroup multiSet = AnnotationGroup.combine(annotationGroupA, annotationGroupB);
        assertEquals(
            Arrays.asList(pa, pb, nb),
            multiSet.annotations()
        );
        assertEquals(
            Arrays.asList(da, db, ndb),
            multiSet.details()
        );
        assertFalse(multiSet.isEmpty());
        assertSame(pa, multiSet.annotation(DatePattern.class));
        assertSame(da, multiSet.detail(DatePatternDetail.class));
        assertSame(nb, multiSet.annotation(NumberPattern.class));
        assertSame(ndb, multiSet.detail(NumberPatternDetail.class));
        assertSame(ndb, multiSet.detailFor(NumberPattern.class));
        assertNull(multiSet.annotation(Nullable.class));
        assertNull(multiSet.detailFor(Nullable.class));
        class D implements AnnotationDetail<Nullable> {

            @Override
            public @NonNull Nullable annotation() {
                return null;
            }
        }
        assertNull(multiSet.detail(D.class));

        assertSame(annotationGroupA, AnnotationGroup.combine(annotationGroupA));
        assertFalse(annotationGroupA.isEmpty());
        assertSame(annotationGroupA, AnnotationGroup.combine(annotationGroupA, AnnotationGroup.empty()));
        assertFalse(annotationGroupA.isEmpty());
        assertSame(AnnotationGroup.empty(), AnnotationGroup.combine());
        assertTrue(AnnotationGroup.empty().isEmpty());
        assertSame(AnnotationGroup.empty(), AnnotationGroup.combine(AnnotationGroup.empty(), AnnotationGroup.empty()));
        assertTrue(AnnotationGroup.empty().isEmpty());
    }

    @Test
    public void testEmptySet() {
        AnnotationGroup empty = AnnotationGroup.empty();
        assertEquals(Collections.emptyList(), empty.annotations());
        assertEquals(Collections.emptyList(), empty.details());
        assertNull(empty.annotation(NumberPattern.class));
        assertNull(empty.detail(DatePatternDetail.class));
        assertNull(empty.detailFor(Nullable.class));
        assertTrue(empty.isEmpty());
    }

    public static class X {

        @NumberPattern("#.0000")
        @DatePattern("yyyy-MM-dd")
        @Nullable
        @A("1")
        @A("2")
        @A("3")
        private String a;

        @NumberPattern
        @DatePattern(zoneId = "Asia/Shanghai")
        private String b;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AS {
        A[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(AS.class)
    public @interface A {
        String value();
    }

    public static class M {

        @DatePattern("yyyy-MM-dd")
        private String a;

        @DatePattern(zoneId = "Asia/Shanghai")
        @NumberPattern("#.0000")
        private String b;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @DetailType(XAnnDetail.class)
    public @interface XAnn {
    }

    public static class XAnnDetail {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @DetailType(YAnnDetail.class)
    public @interface YAnn {
    }

    public static class YAnnDetail implements AnnotationDetail<YAnn> {

        private final YAnn ann;

        public YAnnDetail(YAnn yAnn) {
            this.ann = yAnn;
        }

        @Override
        public @Nonnull YAnn annotation() {
            return ann;
        }
    }

    public static class D {

        @DatePattern("yyyy-MM-dd")
        private String a;

        @NumberPattern("#.0000")
        private String b;

        @XAnn
        private String x;

        @YAnn
        private String y;

        @Nullable
        private String n;
    }
}