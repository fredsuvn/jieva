package samples;

import xyz.fsgik.annotations.DefaultNonNull;
import xyz.fsgik.annotations.DefaultNullable;
import xyz.fsgik.annotations.NonNull;
import xyz.fsgik.annotations.Nullable;

public class Samples {

    //nullable field
    private @Nullable String nullable;

    //non-null field
    private @NonNull String nonNull = "Nonnull";

    //All fields are nullable by default
    @DefaultNullable
    static class NullableClass {
        private String nullable1;
        private String nullable2;
    }

    //All fields are non-null by default
    @DefaultNonNull
    static class NonNullClass {
        private String nonNull1 = "nonNull1";
        private String nonNull2 = "nonNull2";
    }
}
