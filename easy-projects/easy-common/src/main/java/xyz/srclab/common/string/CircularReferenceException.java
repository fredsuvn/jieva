package xyz.srclab.common.string;

import java.util.List;

public class CircularReferenceException extends RuntimeException {

    private final String indexStackTrace;

    public CircularReferenceException(List<?> indexStackTrace) {
        super(StringKit.join(".", indexStackTrace));
        this.indexStackTrace = getMessage();
    }

    public String getIndexStackTrace() {
        return indexStackTrace;
    }
}
