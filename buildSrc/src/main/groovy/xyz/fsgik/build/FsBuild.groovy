package xyz.fsgik.build

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FsBuild implements Plugin<Project> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  static {
    info("FsBuild initialized success.")
    info("Hello from fs-build!")
  }

  static void info(Object... messages) {
    PrintStream out = getPrinter()
    out.print("fs-build >> ")
    out.print("[" + FORMATTER.format(LocalDateTime.now()) + "]: ")
    if (messages != null && messages.length > 0) {
      for (final def msg in messages) {
        out.print(String.valueOf(msg))
      }
    }
    out.println()
  }

  static void debug(Object... messages) {
    PrintStream out = getPrinter()
    out.print("fs-build >> ")
    out.print("[" + FORMATTER.format(LocalDateTime.now()) + "][DEBUG]: ")
    if (messages != null && messages.length > 0) {
      for (final def msg in messages) {
        out.print(String.valueOf(msg))
      }
    }
    out.println()
  }

  static boolean isEnable(Object value) {
    if (value == null) {
      return false
    }
    if (value instanceof Boolean) {
      return value
    }
    String valueToString = value.toString().trim()
    return valueToString.equalsIgnoreCase("true") ||
      valueToString.equalsIgnoreCase("yes") ||
      valueToString.equalsIgnoreCase("enbale") ||
      valueToString.equalsIgnoreCase("1") ||
      valueToString.equalsIgnoreCase("t") ||
      valueToString.equalsIgnoreCase("open")
  }

  @Override
  void apply(Project project) {
    project.getExtensions().add("fs", this)
    project.repositories {
      mavenLocal()
      maven {
        url "https://maven.aliyun.com/repository/central"
      }
      maven {
        url "https://maven.aliyun.com/repository/public"
      }
      mavenCentral()
      //jcenter()
    }
  }

  private static PrintStream getPrinter() {
    return System.out
  }
}
