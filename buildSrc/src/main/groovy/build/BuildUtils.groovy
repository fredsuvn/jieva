package build

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildUtils implements Plugin<Project> {

  static void hello() {
    println "Hello from BuildUtils!"
  }

  @Override
  void apply(Project project) {
    project.getExtensions().add("bu", this)
  }
}
