package test.xyz.srclab.common.id;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.common.id.IdComponentGenerator;
import xyz.srclab.common.id.IdGenerationContext;

/**
 * @author sunqian
 */
public class MyIdComponentGenerator implements IdComponentGenerator<String> {

    private final String value;

    public MyIdComponentGenerator(String value) {
        this.value = value;
    }

    @NotNull
    @Override
    public String name() {
        return "my";
    }

    @Override
    public String generate(@NotNull IdGenerationContext context) {
        return value;
    }
}
