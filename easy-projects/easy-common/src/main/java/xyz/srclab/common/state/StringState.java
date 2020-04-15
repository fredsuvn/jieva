package xyz.srclab.common.state;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface StringState<T extends StringState<T>> extends State<String, String, T> {
}
