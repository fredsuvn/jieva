package xyz.srclab.common;

import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Format;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.run.Running;
import xyz.srclab.common.run.ScheduledRunner;
import xyz.srclab.common.run.ScheduledRunning;

/**
 * @author sunqian
 */
public class Tt {

    private final Format format = null;

    public static void main(String[] args) {
        //As.notNull("");
        //As.nullable("");
        ////IterableOps.IterableOpsCompanion.
        //BaseCollectionOps2.associateValueTo(null, new HashMap<>(), v->null);
        //IterableOps.any(null);
        //ListOps.any(null);
        //
        //List<String> list = Arrays.asList("1", "2", "3");
        //Sequence<String> sequence = CollectionsKt.asSequence(list);
        //SequencesKt.map(sequence, s->s + "2");
        //
        //IterableOps.opsFor(list);
        //BaseIterableOps.to
        //
        //SequencesKt
        //IterableOps.opsFor(null).filterNotNull().
        ListOps<Object> ops = ListOps.opsFor(null)
                .addAll(new Object[0])
                .subList(0)
                .parentList()
                .removeAll(new Object[0]);
        String s = As.asAny(null);
        Running<String> running = null;
        assert running != null;
        String result = running.get();

        new Thread().start();

        ScheduledRunner scheduledRunner = null;
        ScheduledRunning<?> scheduledRunning = scheduledRunner.scheduleAtFixedRate(null, null, null);
    }
}
