package xyz.fslabo.common.exec;

import xyz.fslabo.common.base.BaseBuilder;
import xyz.fslabo.common.base.Jie;

/**
 * {@link Thread}-based Builder of {@link ExecUnit}, to build instance of {@link ExecUnit} of thread.
 *
 * @author fredsuvn
 */
public abstract class ThreadExecBuilder implements BaseBuilder<ThreadExecUnit, ThreadExecBuilder> {

    static ThreadExecBuilder newInstance() {
        return new ThreadExecBuilder.OfJdk8();
    }

    private static int threadInitNumber;

    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    private String name;
    private int priority;
    private boolean daemon;
    private Runnable task;
    private ThreadGroup group;
    private ClassLoader contextClassLoader;
    private long stackSize;

    ThreadExecBuilder() {
        reset();
    }

    /**
     * Sets thread name.
     *
     * @param name thread name
     * @return this
     */
    public ThreadExecBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets thread priority, in [{@link Thread#MIN_PRIORITY}, {@link Thread#MAX_PRIORITY}].
     *
     * @param priority thread priority
     * @return this
     */
    public ThreadExecBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Sets whether the thread is daemon.
     *
     * @param daemon whether the thread is daemon
     * @return this
     */
    public ThreadExecBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * Sets runnable task which is invoked when this thread is started.
     *
     * @param task runnable task
     * @return this
     */
    public ThreadExecBuilder task(Runnable task) {
        this.task = task;
        return this;
    }

    /**
     * Sets context class loader.
     *
     * @param contextClassLoader context class loader
     * @return this
     */
    public ThreadExecBuilder contextClassLoader(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
        return this;
    }

    /**
     * Sets thread group.
     *
     * @param group thread group
     * @return this
     */
    public ThreadExecBuilder group(ThreadGroup group) {
        this.group = group;
        return this;
    }

    /**
     * Sets stack size.
     *
     * @param stackSize stack size
     * @return this
     */
    public ThreadExecBuilder stackSize(long stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    @Override
    public ThreadExecBuilder reset() {
        this.name = null;
        this.priority = -1;
        this.daemon = false;
        this.task = null;
        this.group = null;
        this.contextClassLoader = null;
        this.stackSize = -1;
        return this;
    }

    /**
     * Returns a new {@link ThreadExecUnit} which is not started.
     *
     * @return thread which is configured by this, not started
     * @throws ExecException execution exception
     */
    public ThreadExecUnit build() throws ExecException {
        try {
            String threadName = Jie.orDefault(name, () -> ThreadExecUnit.class.getSimpleName() + "-" + nextThreadNum());
            Thread thread;
            if (stackSize != -1) {
                thread = new Thread(group, task, threadName, stackSize);
            } else {
                thread = new Thread(group, task, threadName);
            }
            if (priority != -1) {
                thread.setPriority(priority);
            }
            thread.setDaemon(daemon);
            if (contextClassLoader != null) {
                thread.setContextClassLoader(contextClassLoader);
            }
            return new ThreadExecUnit(thread);
        } catch (Exception e) {
            throw new ExecException(e);
        }
    }

    private static final class OfJdk8 extends ThreadExecBuilder {
    }
}
