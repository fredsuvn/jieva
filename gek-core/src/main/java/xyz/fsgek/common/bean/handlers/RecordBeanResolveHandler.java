package xyz.fsgek.common.bean.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.collect.GekColl;
import xyz.fsgek.common.base.GekCase;
import xyz.fsgek.common.base.GekString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Resolve handler for the record type object of which getters' name are equal to properties' name,
 * and setters' name are start with "set" (same with {@link JavaBeanResolveHandler}).
 * <p>
 * Note these methods will not be resolved to properties:
 * <ul>
 *     <li>The methods which are declared by {@link Object} and not overridden;</li>
 *     <li>toString() and hashCode();</li>
 * </ul>
 *
 * @author fredsuvn
 */
public class RecordBeanResolveHandler extends AbstractBeanResolveHandler {

    /**
     * An instance.
     */
    public static final RecordBeanResolveHandler INSTANCE = new RecordBeanResolveHandler();

    private final GekCase namingCase = GekCase.LOWER_CAMEL;

    @Nullable
    protected String isGetter(Method method) {
        if (refuse(method)) {
            return null;
        }
        if (method.getParameterCount() == 0) {
            return method.getName();
        }
        return null;
    }

    @Nullable
    protected String isSetter(Method method) {
        if (refuse(method)) {
            return null;
        }
        if (method.getParameterCount() != 1) {
            return null;
        }
        if ((method.getName().length() > 3 && method.getName().startsWith("set"))) {
            List<CharSequence> words = namingCase.split(method.getName());
            if (GekColl.isNotEmpty(words) && words.size() > 1 && GekString.charEquals(words.get(0), "set")) {
                return namingCase.join(words.subList(1, words.size()));
            }
        }
        return null;
    }

    private boolean refuse(Method method) {
        if (Objects.equals(method.getDeclaringClass(), Object.class)) {
            return true;
        }
        if (method.getParameterCount() == 0) {
            if (Objects.equals(method.getName(), "toString")) {
                return true;
            }
            return Objects.equals(method.getName(), "hashCode");
        }
        return false;
    }
}
