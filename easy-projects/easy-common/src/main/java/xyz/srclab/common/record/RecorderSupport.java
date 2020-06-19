package xyz.srclab.common.record;

/**
 * @author sunqian
 */
final class RecorderSupport {

    static Recorder defaultRecorder() {
        return RecorderHolder.INSTANCE;
    }

    private static final class RecorderHolder {

        public static final Recorder INSTANCE = Recorder.newBuilder()
                .resolver(RecordResolver.defaultResolver())
                .build();
    }
}
