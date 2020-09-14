package xyz.srclab.common;

import kotlin.collections.CollectionsKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Format;
import xyz.srclab.common.collection.BaseIterableOps;
import xyz.srclab.common.collection.IterableOps;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.collection.BaseCollectionOps2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author sunqian
 */
public class Tt {

    private final Format format = null;

    public static void main(String[] args) {
        As.notNull("");
        As.nullable("");
        //IterableOps.IterableOpsCompanion.
        BaseCollectionOps2.associateValueTo(null, new HashMap<>(), v->null);
        IterableOps.any(null);
        ListOps.any(null);

        List<String> list = Arrays.asList("1", "2", "3");
        Sequence<String> sequence = CollectionsKt.asSequence(list);
        SequencesKt.map(sequence, s->s + "2");

        IterableOps.opsFor(list);
        BaseIterableOps b = null;
    }
}
