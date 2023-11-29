package xyz.fsgek.common.bean.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekCase;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.collect.GekColl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Resolve handler for the classical java bean, of which getters' name are start with:
 * <ul>
 *     <li>"get" -- for non-boolean return type;</li>
 *     <li>"set" -- for boolean return type;</li>
 * </ul>
 * and setters' name are start with "set".
 *
 * @author fredsuvn
 */
public class JavaBeanResolveHandler extends AbstractBeanResolveHandler {

    /**
     * An instance.
     */
    public static final JavaBeanResolveHandler INSTANCE = new JavaBeanResolveHandler();

    private final GekCase namingCase = GekCase.LOWER_CAMEL;

    @Nullable
    protected String isGetter(Method method) {
        if (method.getParameterCount() != 0) {
            return null;
        }
        if ((method.getName().length() > 3 && method.getName().startsWith("get"))
            || (method.getName().length() > 2 && method.getName().startsWith("is")
            && (Objects.equals(method.getReturnType(), boolean.class)
            || Objects.equals(method.getReturnType(), Boolean.class)))
        ) {
            List<GekCase.Token> tokens = namingCase.tokenize(method.getName());
            if (GekColl.isNotEmpty(tokens) && tokens.size() > 1
                && (
                GekString.charEquals(tokens.get(0).toChars(), "get")
                    || GekString.charEquals(tokens.get(0).toChars(), "is")
            )) {
                return namingCase.join(tokens.subList(1, tokens.size()));
            }
        }
        return null;
    }

    @Nullable
    protected String isSetter(Method method) {
        if (method.getParameterCount() != 1) {
            return null;
        }
        if ((method.getName().length() > 3 && method.getName().startsWith("set"))) {
            List<GekCase.Token> tokens = namingCase.tokenize(method.getName());
            if (GekColl.isNotEmpty(tokens) && tokens.size() > 1
                && GekString.charEquals(tokens.get(0).toChars(), "set")) {
                return namingCase.join(tokens.subList(1, tokens.size()));
            }
        }
        return null;
    }
}
