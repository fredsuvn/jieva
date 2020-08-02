package xyz.srclab.common.cache;

/**
 * @author sunqian
 */
final class NoResultException extends RuntimeException {

    public NoResultException() {
        super(null, null, false, false);
    }
}
