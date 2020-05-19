package xyz.srclab.common.net;

/**
 * @author sunqian
 */
public interface NetReceiver<
        REQ_META, REQ_BODY,
        RES_META, RES_BODY,
        REQ extends NetMessage<REQ_META, REQ_BODY>,
        RES extends NetMessage<RES_META, RES_BODY>
        > {

    void handle(REQ request, NetSender<REQ_META, REQ_BODY, RES_META, RES_BODY, REQ, RES> sender);
}
