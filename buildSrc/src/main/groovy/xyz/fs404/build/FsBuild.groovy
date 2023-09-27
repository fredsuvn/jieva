package xyz.fs404.build

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FsBuild implements Plugin<Project> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  private int _logLevel = 5
  private String _logLevelName = "INFO"

  @Override
  void apply(Project project) {
    project.extensions.add('fs', this)
  }

  void fs(Closure<?> closure) {
    closure()
  }

  void debug(Object... messages) {
    log(1, System.out, messages)
  }

  void info(Object... messages) {
    log(5, System.out, messages)
  }

  void error(Object... messages) {
    log(9, System.err, messages)
  }

  private void log(int level, PrintStream out, Object... messages) {
    if (_logLevel > level) {
      return;
    }
    out.print("fs-build >> ")
    out.print("[" + FORMATTER.format(LocalDateTime.now()) + "][${toLevelInt(this._logLevel)}]: ")
    if (messages != null && messages.length > 0) {
      for (final def msg in messages) {
        out.print(String.valueOf(msg))
      }
    }
    out.println()
  }

  private String toLevelInt(int level) {
    switch (level) {
      case 1: return "DEBUG"
      case 5: return "INFO"
      case 9: return "ERROR"
    }
    return "INFO"
  }

  void setLogLevel(String level) {
    switch (level) {
      case "DEBUG":
        this._logLevel = 1
        break
      case "INFO":
        this._logLevel = 5
        break
      case "ERROR":
        this._logLevel = 9
        break
      default:
        throw new IllegalStateException("Wrong config buildLogLevel: $level")
    }
    this._logLevelName = level
  }
}

