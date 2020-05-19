package xyz.srclab.common.net;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface NetSender<
        REQ_META, REQ_BODY,
        RES_META, RES_BODY,
        REQ extends NetMessage<REQ_META, REQ_BODY>,
        RES extends NetMessage<RES_META, RES_BODY>
        > {

    RES send(REQ request);

    RES send(REQ request, Duration timeout);
}
