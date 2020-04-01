package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;

import java.lang.reflect.Array;
import java.util.function.BiFunction;

public class MapArrayBuilder<OE, NE, A> extends CacheStateBuilder<A> implements ArrayBuilder<A> {

    private final Object array;
    private final Class<?> toElementType;

    private int begin;
    private int end;
    private @Nullable BiFunction<Integer, OE, NE> eachElement;

    MapArrayBuilder(Object array, Class<A> arrayType) {
        if (!array.getClass().isArray()) {
            throw new IllegalStateException("Must be array: " + array);
        }
        this.array = array;
        this.toElementType = arrayType.getComponentType();
        this.begin = 0;
        this.end = Array.getLength(array);
    }

    MapArrayBuilder(Iterable<OE> iterable, Class<A> arrayType) {
        this(ArrayHelper.toArray(iterable, Object.class), arrayType);
    }

    public MapArrayBuilder<OE, NE, A> setBegin(int index) {
        if (index < 0 || index > Array.getLength(array) || index > end) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        this.begin = index;
        return this;
    }

    public MapArrayBuilder<OE, NE, A> setEnd(int index) {
        if (index < 0 || index > Array.getLength(array) || index < begin) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        this.end = index;
        return this;
    }

    public MapArrayBuilder<OE, NE, A> setEachElement(BiFunction<Integer, OE, NE> eachElement) {
        this.eachElement = eachElement;
        return this;
    }

    @Override
    protected A buildNew() {
        A result = (A) Array.newInstance(toElementType, end - begin);
        if (eachElement != null) {
            for (int i = begin, j = 0; i < end; i++, j++) {
                Array.set(result, j, eachElement.apply(j, (OE) Array.get(array, i)));
            }
        }
        return result;
    }
}
