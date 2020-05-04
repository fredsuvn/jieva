package xyz.srclab.common.bytecode.provider.invoke.asm;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.base.Shares;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

public class AsmSupport {

    private static final AtomicLong classCounter = new AtomicLong();

    private static final CharMatcher dotMatcher = Shares.DOT_CHAR_MATCHER;

    private static final CharMatcher $Matcher = Shares.$_CHAR_MATCHER;

    private final Cache<Constructor<?>, ConstructorInvoker<?>> constructorInvokerCache =
            Cache.newGcThreadLocalL2(Cache.newPermanent());

    private final Cache<Method, MethodInvoker> methodInvokerCache =
            Cache.newGcThreadLocalL2(Cache.newPermanent());

    private final Cache<Method, FunctionInvoker> functionInvokerCache =
            Cache.newGcThreadLocalL2(Cache.newPermanent());

    public static String generateConstructorInvokerClassName(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        return generateClassName(declaringClass) + '$' +
                StringUtils.join(
                        ArrayHelper.buildArray(
                                new String[parameterTypes.length], i -> generateClassName(parameterTypes[i])
                        ),
                        '$'
                ) +
                "$CreatedByAsm$" +
                classCounter.getAndIncrement();
    }

    private static String generateClassName(Class<?> cls) {
        String className = cls.getName();
        String className$ = $Matcher.replaceFrom(className, "$$");
        return dotMatcher.replaceFrom(className$, "$");
    }
}
