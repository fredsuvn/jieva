package xyz.srclab.common.net;

/**
 * @author sunqian
 */
public interface NetMessage<META, BODY> {

    META getMeta();

    BODY getBody();
}
