package xyz.fsgek.common.bean.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.collect.FsCollect;
import xyz.fsgek.common.base.FsCase;
import xyz.fsgek.common.base.FsString;

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

    private final FsCase namingCase = FsCase.LOWER_CAMEL;

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
            List<CharSequence> words = namingCase.split(method.getName());
            if (FsCollect.isNotEmpty(words) && words.size() > 1
                && (FsString.charEquals(words.get(0), "get") || FsString.charEquals(words.get(0), "is"))) {
                return namingCase.join(words.subList(1, words.size()));
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
            List<CharSequence> words = namingCase.split(method.getName());
            if (FsCollect.isNotEmpty(words) && words.size() > 1 && FsString.charEquals(words.get(0), "set")) {
                return namingCase.join(words.subList(1, words.size()));
            }
        }
        return null;
    }
}
