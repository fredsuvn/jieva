package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class TypeRefTest {

    @Test
    public void testTypeRef() {
        TypeRef<Map<String, List<? extends String>>> typeRef = new TypeRef<Map<String, List<? extends String>>>() {
        };
        Type type = typeRef.type();
        Assert.assertTrue(type instanceof ParameterizedType);
        ParameterizedType mapType = (ParameterizedType) type;
        Assert.assertEquals(mapType.getRawType(), Map.class);

        Type[] mapActualTypeArguments = mapType.getActualTypeArguments();
        Assert.assertEquals(mapActualTypeArguments[0], String.class);
        Assert.assertTrue(mapActualTypeArguments[1] instanceof ParameterizedType);
        ParameterizedType listType = (ParameterizedType) mapActualTypeArguments[1];
        Assert.assertEquals(listType.getRawType(), List.class);

        Type[] listActualTypeArguments = listType.getActualTypeArguments();
        Assert.assertTrue(listActualTypeArguments[0] instanceof WildcardType);
        WildcardType stringWildcardType = (WildcardType) listActualTypeArguments[0];
        Assert.assertEquals(stringWildcardType.getUpperBounds()[0], String.class);
    }
}
