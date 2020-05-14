package xyz.srclab.common.walk;

/**
 * @author sunqian
 */
public interface WalkerProvider {

    WalkerProvider DEFAULT = new DefaultWalkProvider();

    Walker getWalker(Object walked);
}
