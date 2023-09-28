package xyz.fs404.build

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AbstractBuild implements Plugin<Project> {

  protected Project project;

  private final Set<String> setProperties = new HashSet<>()

  @Override
  void apply(Project project) {
    this.project = project;
    project.extensions.add(getClosureName(), this)
  }

  abstract String getClosureName()

  void initConfiguration(String... configs) {
    for (final def config in configs) {
      if (setProperties.contains(config)) {
        continue
      }
      String gradleConfigName = "fs" + config.capitalize()
      def gradleConfig = project.findProperty(gradleConfigName)
      if (gradleConfig != null) {
        this[config] = gradleConfig.toString()
        afterConfiguration(config)
      }
    }
  }

  void afterConfiguration(String config) {
    setProperties.add(config)
  }
}
