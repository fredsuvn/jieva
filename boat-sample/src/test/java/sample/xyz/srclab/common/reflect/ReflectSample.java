package sample.xyz.srclab.common.reflect;

import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.reflect.Types;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class ReflectSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testReflects() {
        Method method = Reflects.method(Object.class, "toString");
        String s = Reflects.invoke(method, new Object());
        //java.lang.Object@97c879e
        logger.log("s: {}", s);
    }

    @Test
    public void testTypes() {
        ParameterizedType type = Types.parameterizedType(List.class, String.class);
        GenericArrayType arrayType = Types.genericArrayType(type);
        //java.util.List<java.lang.String>[]
        logger.log("arrayType: {}", arrayType);
    }
}
