package xyz.fsgik.build

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AbstractFsPlugin implements Plugin<Project> {

  protected Project project;

  private final Set<String> configurations = new HashSet<>()

  @Override
  void apply(Project project) {
    this.project = project;
    project.extensions.add(getClosureName(), this)
  }

  protected abstract String getClosureName()

  protected void beforeConfiguration(String... configs) {
    for (final def config in configs) {
      if (configurations.contains(config)) {
        continue
      }
      String gradleConfigName = "fs" + config.capitalize()
      def gradleConfig = project.findProperty(gradleConfigName)
      if (gradleConfig != null) {
        this[config] = gradleConfig.toString()
        updateConfiguration(config)
      }
    }
  }

  protected void updateConfiguration(String config) {
    configurations.add(config)
  }
}
