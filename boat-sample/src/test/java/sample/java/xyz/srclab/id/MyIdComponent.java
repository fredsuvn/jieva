package sample.java.xyz.srclab.id;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.common.id.IdComponent;
import xyz.srclab.common.id.IdContext;

import java.util.List;

/**
 * @author sunqian
 */
public class MyIdComponent implements IdComponent<String> {

    public static final String TYPE = "My";

    private String value;

    @NotNull
    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void init(@NotNull List<?> args) {
        this.value = String.valueOf(args.get(0));
    }

    @Override
    public String newValue(@NotNull IdContext context) {
        return value;
    }
}
