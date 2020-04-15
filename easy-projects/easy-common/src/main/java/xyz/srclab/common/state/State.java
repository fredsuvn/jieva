package xyz.srclab.common.state;

import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.Nullable;

@Immutable
public interface State<Code, Description, T extends State<Code, Description, T>> {

    Code getCode();

    @Nullable
    Description getDescription();

    T withMoreDescription(Description moreDescription);
}
