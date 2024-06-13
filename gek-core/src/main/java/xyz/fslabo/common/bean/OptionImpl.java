package xyz.fslabo.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
final class OptionImpl implements BeanOption {

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