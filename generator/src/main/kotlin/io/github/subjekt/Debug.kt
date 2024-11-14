package io.github.subjekt

import java.nio.file.Path

object Debug {

  @JvmStatic
  fun main(vararg args: String) {
    Subjekt.resource("subjects/ExampleSuite.yaml").outcomes.also { println(it) }
      //.toKotlinSources(Path.of("./"))
  }
}
