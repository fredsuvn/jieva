package xyz.fsgek.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

final class Impls {

    static CopierImpl DEFAULT_COPIER = new CopierImpl();

    static GekDataOption newGekDataOption(GekDataOption.Key key, Object value) {
        return new GekDataOptionImpl(key, value);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class GekDataOptionImpl implements GekDataOption {

        private final Key key;
        private final Object value;

        @Override
        public Key getKey() {
            return null;
        }

        @Override
        public Object getValue() {
            return null;
        }
    }
}
