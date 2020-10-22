package xyz.srclab.common;

import kotlin.Lazy;
import kotlin.LazyKt;
import xyz.srclab.common.base.*;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.exception.ExceptionStatus;
import xyz.srclab.common.exception.StatusException;
import xyz.srclab.common.reflect.MethodKit;
import xyz.srclab.common.run.*;

import java.util.ArrayList;
import java.util.List;

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
                .subList(1)
                .removeAll(new Object[0]);
        String s = As.any(null);
        Running<String> running = null;
        assert running != null;
        String result = running.get();

        new Thread().start();

        ScheduledRunner scheduledRunner = null;
        ScheduledRunning<?> scheduledRunning = scheduledRunner.scheduleAtFixedRate(null, null, null);

        new ThreadPoolRunner.Builder().corePoolSize(1).build();

        new StatusException((Throwable) null).code();

        ExceptionStatus exceptionStatus = null;
        exceptionStatus.code();

        System.out.println(Defaults.charset());

        Environment.defaultEnvironment();

        Lazy<String> lazy = LazyKt.lazy(() -> "");
        lazy.getValue();

        Object uv = Parts.UNINITIALIZED_VALUE;

        Runner r = Runner.syncRunner();

        List<String> stringList = new ArrayList<>();

        AboutBoat.aboutBoat();

        ExceptionStatus internal = ExceptionStatus.INTERNAL;

        String parts = Parts.UNINITIALIZED_VALUE;

        MethodKit.isPublic(null);
        MethodKit.invokeVirtual(null);

        Format.FAST_FORMAT.format("");
    }
}
