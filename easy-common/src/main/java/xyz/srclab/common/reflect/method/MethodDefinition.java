package xyz.srclab.common.reflect.method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public interface MethodDefinition<T> {

    static <T> Builder<T> newBuilding() {
        return new Builder<>();
    }

    Class<?> getDeclaringClass();

    String getName();

    Class<?>[] getParameterTypes();

    Class<T> getReturnType();

    MethodBody<T> getBody();

    class Builder<T> {

        private Class<?> declaringClass;
        private String name;
        private Class<?>[] parameterTypes;
        private Class<T> returnType;
        private MethodBody<T> body;

        public Builder<T> setDeclaringClass(Class<?> declaringClass) {
            this.declaringClass = declaringClass;
            return this;
        }

        public Builder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> setParameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        public Builder<T> setReturnType(Class<T> returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder<T> setBody(MethodBody<T> body) {
            this.body = body;
            return this;
        }

        public MethodDefinition<T> build() {
            if (declaringClass == null) {
                throw new IllegalStateException("No declaring class.");
            }
            if (StringUtils.isEmpty(name)) {
                throw new IllegalStateException("Name is empty or null.");
            }
            if (returnType == null) {
                throw new IllegalStateException("No return type.");
            }
            if (body == null) {
                throw new IllegalStateException("No method body.");
            }
            if (parameterTypes == null) {
                parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
            }

            return new MethodDefinition<T>() {
                @Override
                public Class<?> getDeclaringClass() {
                    return declaringClass;
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Class<?>[] getParameterTypes() {
                    return parameterTypes;
                }

                @Override
                public Class<T> getReturnType() {
                    return returnType;
                }

                @Override
                public MethodBody<T> getBody() {
                    return body;
                }
            };
        }
    }
}
