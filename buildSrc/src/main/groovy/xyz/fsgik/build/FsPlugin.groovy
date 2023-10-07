package xyz.fsgik.build

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FsPlugin extends AbstractFsPlugin {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  private int logLevelValue = 3

  @Override
  String getClosureName() {
    return "fs"
  }

  void debug(Object... messages) {
    log(1, System.out, messages)
  }

  void info(Object... messages) {
    log(3, System.out, messages)
  }

  void warn(Object... messages) {
    log(5, System.out, messages)
  }

  void error(Object... messages) {
    log(7, System.err, messages)
  }

  private void log(int level, PrintStream out, Object... messages) {
    beforeConfiguration("logLevel")
    if (logLevelValue > level) {
      return;
    }
    out.print("fs-build >> ")
    String prefix = "[" + FORMATTER.format(LocalDateTime.now()) + "] [${levelValueToName(level)}]";
    out.print(String.format("%-33s", prefix))
    out.print(": ")
    if (messages != null && messages.length > 0) {
      for (final def msg in messages) {
        out.print(String.valueOf(msg))
      }
    }
    out.println()
  }

  void setLogLevel(String levelName) {
    this.logLevelValue = levelNameToValue(levelName)
    updateConfiguration("logLevel")
  }

  private static String levelValueToName(int levelValue) {
    switch (levelValue) {
      case 1: return "DEBUG"
      case 3: return "INFO"
      case 5: return "WARN"
      case 7: return "ERROR"
    }
    return "INFO"
  }

  private static int levelNameToValue(String name) {
    switch (name) {
      case "DEBUG": return 1
      case "INFO": return 3
      case "WARN": return 5
      case "ERROR": return 7
      default:
        throw new IllegalStateException("Wrong config buildLogLevel: $name")
    }
  }
}

