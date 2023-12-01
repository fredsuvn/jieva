package xyz.fsgek.common.base;

import xyz.fsgek.common.io.GekIOException;

/**
 * This class is used to configure and start a {@link Thread} in method chaining:
 * <pre>
 *     thread.name(name).priority(5).start();
 * </pre>
 * Its instance is reusable, re-set and re-start are permitted.
 *
 * @author fredsuvn
 */
public abstract class GekThread {

    static GekThread newInstance() {
        return new GekThread.OfJdk8();
    }

    private static int threadInitNumber;

    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    private String name;
    private int priority = -1;
    private boolean daemon = false;
    private Runnable task;
    private ThreadGroup group;
    private ClassLoader contextClassLoader;
    private long stackSize = -1;

    GekThread() {
    }

    /**
     * Sets thread name.
     *
     * @param name thread name
     * @return this
     */
    public GekThread name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets thread priority, in [{@link Thread#MIN_PRIORITY}, {@link Thread#MAX_PRIORITY}].
     *
     * @param priority thread priority
     * @return this
     */
    public GekThread priority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Sets whether the thread is daemon.
     *
     * @param daemon whether the thread is daemon
     * @return this
     */
    public GekThread daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * Sets runnable task which is invoked when this thread is started.
     *
     * @param task runnable task
     * @return this
     */
    public GekThread task(Runnable task) {
        this.task = task;
        return this;
    }

    /**
     * Sets context class loader.
     *
     * @param contextClassLoader context class loader
     * @return this
     */
    public GekThread contextClassLoader(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
        return this;
    }

    /**
     * Sets thread group.
     *
     * @param group thread group
     * @return this
     */
    public GekThread group(ThreadGroup group) {
        this.group = group;
        return this;
    }

    /**
     * Sets stack size.
     *
     * @param stackSize stack size
     * @return this
     */
    public GekThread stackSize(long stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    /**
     * Clears current configurations.
     *
     * @return this
     */
    public GekThread clear() {
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
     * Returns thread which is configured by this, not started.
     *
     * @return thread which is configured by this, not started
     * @throws GekIOException IO exception
     */
    public Thread build() throws GekIOException {
        try {
            String threadName = Gek.notNull(name, () -> "GekThread-" + nextThreadNum());
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
            return thread;
        } catch (Exception e) {
            throw new GekIOException(e);
        }
    }

    /**
     * Starts and returns new thread which is configured by this.
     *
     * @return the thread which is started
     * @throws GekIOException IO exception
     */
    public Thread start() throws GekIOException {
        try {
            Thread thread = build();
            thread.start();
            return thread;
        } catch (GekIOException e) {
            throw e;
        } catch (Exception e) {
            throw new GekIOException(e);
        }
    }

    private static final class OfJdk8 extends GekThread {
    }
}
