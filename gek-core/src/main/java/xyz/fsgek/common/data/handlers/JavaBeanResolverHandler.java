package xyz.fsgek.common.data.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekCase;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.data.GekDataResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * {@link GekDataResolver.Handler} for the classical java bean, of which getters' names are start with:
 * <ul>
 *     <li>"get" -- for non-boolean returned type;</li>
 *     <li>"is" -- for boolean returned type;</li>
 * </ul>
 * and setters' names are start with "set".
 *
 * @author fredsuvn
 */
public class JavaBeanResolverHandler extends AbstractDataResolverHandler {

    /**
     * An instance.
     */
    public static final JavaBeanResolverHandler INSTANCE = new JavaBeanResolverHandler();

    private final GekCase namingCase = GekCase.LOWER_CAMEL;

    @Nullable
    protected Getter resolveGetter(Method method) {
        if (method.getParameterCount() != 0) {
            return null;
        }
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
        boolean isGetter = false;
        // getXxx
        if (methodName.length() > 3 && methodName.startsWith("get")
            && !Objects.equals(returnType, boolean.class) && !Objects.equals(returnType, Boolean.class)) {
            isGetter = true;
        }
        // isXxx
        else if (methodName.length() > 2 && methodName.startsWith("is")
            && (Objects.equals(returnType, boolean.class) || Objects.equals(returnType, Boolean.class))) {
            isGetter = true;
        }
        if (!isGetter) {
            return null;
        }
        List<GekCase.Token> tokens = namingCase.tokenize(methodName);
        if (tokens.size() > 1 && (
            GekString.charEquals(tokens.get(0).toChars(), "get")
                || GekString.charEquals(tokens.get(0).toChars(), "is")
        )) {
            return buildGetter(namingCase.join(tokens.subList(1, tokens.size())), method);
        }
        return null;
    }

    @Nullable
    protected Setter resolveSetter(Method method) {
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
