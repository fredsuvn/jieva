package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsCase;
import xyz.srclab.common.collect.FsCollect;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Resolver for {@link FsBean}.
 *
 * @author sunq62
 */
public interface FsBeanResolver {

    /**
     * Resolves given type to {@link FsBean}.
     *
     * @param type given type
     */
    FsBean resolve(Type type);

    class Builder {

        /**
         * Reflect mode.
         */
        public static final int USE_REFLECT = 1;
        /**
         * Un-reflect mode.
         */
        public static final int USE_UNREFLECT = 2;
        /**
         * Bean naming.
         */
        public static final int BEAN_NAMING = 1;
        /**
         * Record naming.
         */
        public static final int RECORD_NAMING = 2;

        private int invokeMode = USE_REFLECT;
        private int propertyNaming = BEAN_NAMING;

        /**
         * Sets invoke mode for getter and setter methods:
         * <ul>
         *     <li>
         *         {@link #USE_REFLECT}: Using reflect to invoke getter and setter methods.
         *     </li>
         *     <li>
         *         {@link #USE_UNREFLECT}: Using un-reflect ({@link java.lang.invoke.MethodHandles})
         *         to invoke getter and setter methods.
         *     </li>
         * </ul>
         *
         * @param invokeMode invoke mode
         */
        public Builder invokeMode(int invokeMode) {
            this.invokeMode = invokeMode;
            return this;
        }

        /**
         * Sets property naming for getter and setter methods:
         * <ul>
         *     <li>
         *         {@link #BEAN_NAMING}: Starting with "get"/"set" for each getter and setter methods.
         *     </li>
         *     <li>
         *         {@link #RECORD_NAMING}: Directly using property name for getter methods,
         *         and starting with "set" for setter methods.
         *     </li>
         * </ul>
         *
         * @param propertyNaming property naming
         */
        public Builder propertyNaming(int propertyNaming) {
            this.propertyNaming = propertyNaming;
            return this;
        }


        private static final class FsBeanResolverImpl implements FsBeanResolver {

            private final int invokeMode;
            private final int propertyNaming;

            private FsBeanResolverImpl(int invokeMode, int propertyNaming) {
                this.invokeMode = invokeMode;
                this.propertyNaming = propertyNaming;
            }

            @Override
            public FsBean resolve(Type type) {
                Method[] methods;
                if (type instanceof Class) {
                    methods = ((Class<?>) type).getMethods();
                } else if (type instanceof ParameterizedType) {
                    methods = ((Class<?>) ((ParameterizedType) type).getRawType()).getMethods();
                } else {
                    throw new IllegalArgumentException("The type to be resolved must be Class or ParameterizedType.");
                }
                Map<String, Method> getters = new LinkedHashMap<>();
                Map<String, Method> setters = new LinkedHashMap<>();
                for (Method method : methods) {
                    String propertyName = isGetter(method);
                    if (propertyName != null) {
                        getters.put(propertyName, method);
                        continue;
                    }
                    propertyName = isSetter(method);
                    if (propertyName != null) {
                        setters.put(propertyName, method);
                    }
                }
                return null;
            }

            @Nullable
            private String isGetter(Method method) {
                switch (propertyNaming) {
                    case BEAN_NAMING:
                        if (method.getName().length() > 3 && method.getName().startsWith("get")) {
                            List<CharSequence> words = FsCase.LOWER_CAMEL.split(method.getName());
                            if (!FsCollect.isEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "get")) {
                                return FsCase.LOWER_CAMEL.join(words.subList(1, words.size()));
                            }
                        }
                        return null;
                    case RECORD_NAMING:
                        return method.getName();
                    default:
                        throw new IllegalStateException("Unknown property naming: " + propertyNaming);
                }
            }

            @Nullable
            private String isSetter(Method method) {
                switch (propertyNaming) {
                    case BEAN_NAMING:
                    case RECORD_NAMING:
                        if (method.getName().length() > 3 && method.getName().startsWith("set")) {
                            List<CharSequence> words = FsCase.LOWER_CAMEL.split(method.getName());
                            if (!FsCollect.isEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "set")) {
                                return FsCase.LOWER_CAMEL.join(words.subList(1, words.size()));
                            }
                        }
                        return null;
                    default:
                        throw new IllegalStateException("Unknown property naming: " + propertyNaming);
                }
            }

//            private FsBeanProperty buildProperty(String name, Method getter) {
//
//            }
        }
    }
}
