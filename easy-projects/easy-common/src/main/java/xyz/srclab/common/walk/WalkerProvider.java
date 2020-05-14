package xyz.srclab.common.walk;

/**
 * @author sunqian
 */
public interface WalkerProvider {

    static WalkerProvider DEFAULT = new DefaultWalkProvider();

    Walker getWalker(Object walked);
}
