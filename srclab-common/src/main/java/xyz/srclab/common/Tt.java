package xyz.srclab.common;

import xyz.srclab.common.base.*;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.exception.CommonException;
import xyz.srclab.common.exception.ExceptionStatus;
import xyz.srclab.common.run.Running;
import xyz.srclab.common.run.ScheduledRunner;
import xyz.srclab.common.run.ScheduledRunning;
import xyz.srclab.common.run.ThreadPoolRunner;

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

        new ThreadPoolRunner.Builder().corePoolSize(1).build();

        new CommonException((Throwable) null).code();

        ExceptionStatus exceptionStatus = null;
        exceptionStatus.code();

        System.out.println(Defaults.charset());

        About about = About.current();
        about.name();

        Environment.fileSeparator();
    }
}
