package xyz.srclab.common.util.string.tostring;

import java.util.List;

public class PropertyOrElementReferenceLoopException extends RuntimeException {

    private static String buildNameStackTrace(List<String> nameStackTrace) {
        return String.join("", nameStackTrace);
    }

    private final String nameStackTrace;

    public PropertyOrElementReferenceLoopException(List<String> nameStackTrace) {
        super(buildNameStackTrace(nameStackTrace));
        this.nameStackTrace = getMessage();
    }

    public String getNameStackTrace() {
        return nameStackTrace;
    }
}
