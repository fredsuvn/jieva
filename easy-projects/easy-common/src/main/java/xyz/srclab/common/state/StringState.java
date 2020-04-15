package xyz.srclab.common.state;

import xyz.srclab.annotations.Immutable;

@Immutable
public interface StringState<T extends StringState<T>> extends State<String, String, T> {
}
