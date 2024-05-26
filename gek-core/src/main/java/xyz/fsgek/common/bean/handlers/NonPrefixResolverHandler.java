package xyz.fsgek.common.bean.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.bean.GekBeanResolver;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * {@link GekBeanResolver.Handler} for non-prefix style, of which getters' and setters' names are themselves.
 * For example:
 * <pre>
 *     // Getter of "foo"
 *     someData.foo();
 *     // Setter of "foo"
 *     someData.foo(String foo);
 * </pre>
 * Note the name cannot same with any name of method in {@link Object} such as "toString", "hashCode", "clone", etc.
 *
 * @author fredsuvn
 */
public class NonPrefixResolverHandler extends AbstractBeanResolverHandler {

    private static final Method[] BELONG_OBJECT = Object.class.getMethods();

    /**
     * An instance.
     */
    public static final NonPrefixResolverHandler INSTANCE = new NonPrefixResolverHandler();

    @Nullable
    protected Getter resolveGetter(Method method) {
        if (belongObject(method)) {
            return null;
        }
        if (method.getParameterCount() == 0) {
            return buildGetter(method.getName(), method);
        }
        return null;
    }

    @Nullable
    protected Setter resolveSetter(Method method) {
        if (belongObject(method)) {
            return null;
        }
        if (method.getParameterCount() == 1) {
            return buildSetter(method.getName(), method);
        }
        return null;
    }

    protected boolean belongObject(Method method) {
        for (Method objMethod : BELONG_OBJECT) {
            if (Objects.equals(objMethod.getName(), method.getName())) {
                return true;
            }
        }
        return false;
    }
}
