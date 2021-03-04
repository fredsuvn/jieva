package test.xyz.srclab.common.id;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.common.id.IdComponentFactory;
import xyz.srclab.common.id.IdContext;

/**
 * @author sunqian
 */
public class MyIdComponentFactory implements IdComponentFactory<String> {

    private final String value;

    public MyIdComponentFactory(String value) {
        this.value = value;
    }

    @Override
    public String create(@NotNull IdContext context) {
        return value;
    }
}
