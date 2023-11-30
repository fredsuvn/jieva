package xyz.fsgek.common.collect;

/**
 * This class is used to configure and build collection in method chaining:
 * <pre>
 *     process.input(in).output(out).start();
 * </pre>
 *
 * @author fredsuvn
 */
public abstract class GekCollect {

    static GekCollect newInstance() {
        return new OfJdk8();
    }

    private static final class OfJdk8 extends GekCollect {
    }
}
