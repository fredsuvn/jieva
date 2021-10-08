package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.AnyAccessor;
import xyz.srclab.common.base.GenericAccessor;
import xyz.srclab.common.base.GenericMapAccessor;
import xyz.srclab.common.base.MapAccessor;

import java.util.HashMap;
import java.util.Map;

public class AccessorTest {

    @Test
    public void testAnyAccessor() {
        TestAnyAccessor singleAccessor = new TestAnyAccessor();
        Assert.assertNull(singleAccessor.getOrNull());
        Assert.assertEquals("666", singleAccessor.getOrElse("666"));
        Assert.assertEquals("666", singleAccessor.getOrElse(() -> "666"));
        singleAccessor.set("777");
        Assert.assertEquals("777", singleAccessor.get());
    }

    @Test
    public void testGenericAccessor() {
        TestGenericAccessor singleAccessor = new TestGenericAccessor();
        Assert.assertNull(singleAccessor.getOrNull());
        Assert.assertEquals("666", singleAccessor.getOrElse("666"));
        Assert.assertEquals("666", singleAccessor.getOrElse(() -> "666"));
        singleAccessor.set("777");
        Assert.assertEquals("777", singleAccessor.get());
    }

    @Test
    public void testMapAccessor() {
        TestMapAccessor mapAccessor = new TestMapAccessor();
        Assert.assertNull(mapAccessor.getOrNull("1"));
        Assert.assertEquals("666", mapAccessor.getOrElse("1", "666"));
        Assert.assertEquals("666", mapAccessor.getOrElse("1", (k) -> "666"));
        mapAccessor.set("1", "777");
        Assert.assertEquals("777", mapAccessor.get("1"));
    }

    @Test
    public void testGenericMapAccessor() {
        TestGenericMapAccessor mapAccessor = new TestGenericMapAccessor();
        Assert.assertNull(mapAccessor.getOrNull("1"));
        Assert.assertEquals("666", mapAccessor.getOrElse("1", "666"));
        Assert.assertEquals("666", mapAccessor.getOrElse("1", (k) -> "666"));
        mapAccessor.set("1", "777");
        Assert.assertEquals("777", mapAccessor.get("1"));
    }

    public static class TestAnyAccessor implements AnyAccessor {

        private String value;

        @Override
        public <T> T getOrNull() {
            return (T) value;
        }

        @Override
        public void set(@Nullable Object value) {
            this.value = (String) value;
        }
    }

    public static class TestGenericAccessor implements GenericAccessor<String> {

        private String value;

        @Override
        public String getOrNull() {
            return value;
        }

        @Override
        public void set(@Nullable String value) {
            this.value = value;
        }
    }

    public static class TestMapAccessor implements MapAccessor {

        private final Map<Object, Object> values = new HashMap<>();

        @Override
        public @NotNull Map<Object, Object> asMap() {
            return values;
        }
    }

    public static class TestGenericMapAccessor implements GenericMapAccessor<String, String> {

        private final Map<String, String> values = new HashMap<>();

        @Override
        public @NotNull Map<String, String> asMap() {
            return values;
        }
    }
}
