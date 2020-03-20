package xyz.srclab.common.state;

import org.jetbrains.annotations.Nullable;

public interface State<Code, Description, T extends State<Code, Description, T>> {

    Code getCode();

    @Nullable
    Description getDescription();

    T withMoreDescription(Description moreDescription);
}
