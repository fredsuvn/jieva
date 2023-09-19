package xyz.srclab.common.net;

/**
 * States for TCP/IP connection.
 *
 * @author fredsuvn
 */
public class FsTcpStates {

    /**
     * Created state: the channel or endpoint never open or close.
     * This is the initialized state.
     */
    public static final int CREATED = 0;
    /**
     * Opened state: the channel or endpoint has been opened.
     */
    public static final int OPENED = 1;
    /**
     * Closed state: the channel or endpoint has been closed.
     */
    public static final int CLOSED = 2;

    private int state = CREATED;

    /**
     * Returns whether current state is {@link #CREATED}.
     */
    public boolean isCreated() {
        return state == CREATED;
    }

    /**
     * Returns whether current state is {@link #OPENED}.
     */
    public boolean isOpened() {
        return state == OPENED;
    }

    /**
     * Returns whether current state is {@link #CLOSED}.
     */
    public boolean isClosed() {
        return state == CLOSED;
    }

    /**
     * Returns current state: {@link #CREATED}, {@link #OPENED} or {@link #CLOSED}.
     */
    public int getState() {
        return state;
    }

    /**
     * Sets state to {@link #OPENED}.
     */
    public void open() {
        state = OPENED;
    }

    /**
     * Sets state to {@link #CLOSED}.
     */
    public void close() {
        state = CLOSED;
    }
}
