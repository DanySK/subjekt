/*
 * This file was generated by the Gradle 'init' task.
 *
 * This settings file is used to specify which projects to include in your build-logic build.
 */

rootProject.name = "buildSrc"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
