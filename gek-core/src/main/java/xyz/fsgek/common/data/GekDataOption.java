package xyz.fsgek.common.data;

import xyz.fsgek.common.base.GekOption;

/**
 * Options for get data operation.
 *
 * @author sunqian
 */
public class GekDataOption implements GekOption<Integer, Object> {

    private final Integer key;
    private final Object value;

    /**
     * Constructs with key and value.
     *
     * @param key   the key
     * @param value the value
     */
    public GekDataOption(Integer key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Integer key() {
        return key;
    }

    @Override
    public Object value() {
        return value;
    }
}
