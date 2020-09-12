package xyz.srclab.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Format;
import xyz.srclab.common.collection.OpsForCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class Tt {

    private final Format format = null;

    public static void main(String[] args) {
        As.notNull("");
        As.nullable("");
        //IterableOps.IterableOpsCompanion.
        OpsForCollection.associateValueTo(null, new HashMap<>(), v->null);
    }
}
