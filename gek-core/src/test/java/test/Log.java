package test;

import xyz.fslabo.common.base.JieLog;

public class Log {

    public static void log(Object... message) {
        JieLog.system().info(message);
    }
}
