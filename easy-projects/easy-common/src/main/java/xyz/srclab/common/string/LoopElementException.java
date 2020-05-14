package xyz.srclab.common.string;

import java.util.List;

public class LoopElementException extends RuntimeException {

    private final String nameStackTrace;

    public LoopElementException(List<Object> nameStackTrace) {
        super(StringHelper.join(".", nameStackTrace));
        this.nameStackTrace = getMessage();
    }

    public String getNameStackTrace() {
        return nameStackTrace;
    }
}
