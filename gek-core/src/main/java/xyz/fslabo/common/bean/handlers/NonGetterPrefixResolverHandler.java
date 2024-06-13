package xyz.fslabo.common.bean.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.GekCase;
import xyz.fslabo.common.base.GekString;
import xyz.fslabo.common.bean.GekBeanResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * {@link GekBeanResolver.Handler} for non-getter-prefix style (but setters have),
 * of which getters' names are themselves, and setters' names are start with "set".
 * For example:
 * <pre>
 *     // Getter of "foo"
 *     someData.foo();
 *     // Setter of "foo"
 *     someData.setFoo(String foo);
 * </pre>
 * Note the name cannot same with any name of method in {@link Object} such as "toString", "hashCode", "clone", etc.
 *
 * @author fredsuvn
 */
public class NonGetterPrefixResolverHandler extends NonPrefixResolverHandler {

    /**
     * An instance.
     */
    public static final NonGetterPrefixResolverHandler INSTANCE = new NonGetterPrefixResolverHandler();

    private final GekCase namingCase = GekCase.LOWER_CAMEL;

    @Nullable
    protected Setter resolveSetter(Method method) {
        if (belongObject(method)) {
            return null;
        }
        if (method.getParameterCount() != 1) {
            return null;
        }
        String methodName = method.getName();
        boolean isSetter = methodName.length() > 3 && methodName.startsWith("set");
        // setXxx
        if (!isSetter) {
            return null;
        }
        List<GekCase.Token> tokens = namingCase.tokenize(methodName);
        if (tokens.size() > 1 && GekString.charEquals(tokens.get(0).toChars(), "set")) {
            return buildSetter(namingCase.join(tokens.subList(1, tokens.size())), method);
        }
        return null;
    }
}
