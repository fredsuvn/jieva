package test;

import lombok.Data;
import xyz.fs404.annotations.Nullable;

/**
 * @author sunqian
 */
@Data
public class Bean {
    private @Nullable String p1;
    private String p2 = "p2";
}
