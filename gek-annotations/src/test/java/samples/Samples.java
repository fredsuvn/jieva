package samples;

import xyz.fsgek.annotations.DefaultNonNull;
import xyz.fsgek.annotations.DefaultNullable;
import xyz.fsgek.annotations.NonNull;
import xyz.fsgek.annotations.Nullable;

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
