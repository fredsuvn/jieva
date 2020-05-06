package xyz.srclab.common.util.string.tostring;

import xyz.srclab.common.util.string.StringHelper;

import java.util.List;

public class PropertyOrElementReferenceLoopException extends RuntimeException {

    private static String buildNameStackTrace(List<String> nameStackTrace) {
        return StringHelper.join("", nameStackTrace);
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
