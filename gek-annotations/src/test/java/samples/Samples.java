package samples;

import xyz.fslabo.annotations.*;

public class Samples {

    // Nullable field
    private @Nullable String nullable;

    // Non-null field
    private final @NonNull String nonNull = "Nonnull";

    // All fields are nullable by default
    @DefaultNullable
    static class NullableClass {
        private String nullable1;
        private String nullable2;
    }

    // All fields are non-null by default
    @DefaultNonNull
    static class NonNullClass {
        private final String nonNull1 = "nonNull1";
        private final String nonNull2 = "nonNull2";
    }
}
