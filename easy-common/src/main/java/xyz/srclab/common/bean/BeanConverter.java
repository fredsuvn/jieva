package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.builder.ProcessByHandlersBuilder;
import xyz.srclab.common.format.FormatHelper;
import xyz.srclab.common.lang.TypeRef;

import java.lang.reflect.Type;

public interface BeanConverter {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    @Nullable
    <T> T convert(@Nullable Object from, Type to);

    @Nullable
    <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator);

    @Nullable
    default <T> T convert(@Nullable Object from, Class<T> to) {
        return convert(from, (Type) to);
    }

    @Nullable
    default <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
        return convert(from, (Type) to, beanOperator);
    }

    @Nullable
    default <T> T convert(@Nullable Object from, TypeRef<T> to) {
        return convert(from, to.getType());
    }

    @Nullable
    default <T> T convert(@Nullable Object from, TypeRef<T> to, BeanOperator beanOperator) {
        return convert(from, to.getType(), beanOperator);
    }

    class Builder extends ProcessByHandlersBuilder<BeanConverter, BeanConverterHandler, Builder> {

        public static Builder newBuilder() {
            return new Builder();
        }

        protected BeanConverter buildNew() {
            return new BeanConverterImpl(this);
        }

        private static class BeanConverterImpl implements BeanConverter {

            private final BeanConverterHandler[] handlers;

            private BeanConverterImpl(Builder builder) {
                this.handlers = builder.handlers.toArray(new BeanConverterHandler[0]);
            }

            @Nullable
            @Override
            public <T> T convert(@Nullable Object from, Type to) {
                return convert(from, to, CommonBeanOperator.getInstance());
            }

            @Nullable
            @Override
            public <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator) {
                for (BeanConverterHandler handler : handlers) {
                    if (handler.supportConvert(from, to, beanOperator)) {
                        return handler.convert(from, to, beanOperator);
                    }
                }
                throw new UnsupportedOperationException(
                        FormatHelper.fastFormat("Cannot convert object from {} to {}", from, to));
            }
        }
    }
}
