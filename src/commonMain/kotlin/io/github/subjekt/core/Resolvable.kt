/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.utils.Utils.format

/**
 * Core entity used to represent a resolvable value. This is used to represent the values that can be resolved inside
 * a Suite with a proper [io.github.subjekt.core.definition.Context].
 */
class Resolvable
    @Throws(IllegalArgumentException::class)
    constructor(
        /**
         * Source from which the resolvable is parsed.
         */
        val source: String,
        expressionPrefix: String = "\${{",
        expressionSuffix: String = "}}",
    ) {
        private val resolvableString: ResolvableString =
            source.parseToResolvableString(
                expressionPrefix,
                expressionSuffix,
            )

        /**
         * Expressions contained in this Resolvable.
         */
        val expressions: List<RawExpression> by lazy {
            resolvableString.expressions
        }

        /**
         * Internal class to handle string substitution with expressions.
         */
        internal class ResolvableString(
            /**
             * The string that can be formatted with the resolved expressions.
             */
            val toFormat: String,
            /**
             * The list of [RawExpression]s that can be resolved inside this string.
             */
            val expressions: List<RawExpression>,
        ) {
            /**
             * Formats the string with the given [values]. Throws an [IllegalArgumentException] if the [values] number
             * it not equal to the number of [expressions].
             */
            @Throws(IllegalArgumentException::class)
            fun format(vararg values: Any): String {
                require(values.size == expressions.size) {
                    "Number of values does not match number of expressions"
                }
                return toFormat.format(*values)
            }
        }

        /**
         * Simple utility class to represent an expression that has not been parsed yet.
         */
        class RawExpression(
            val source: String,
        )

        companion object {
            /**
             * Parses a string into a [ResolvableString] object. This is used to parse a string that contains
             * expressions into an intermediate class that handles string substitution and other operations.
             */
            @Throws(IllegalArgumentException::class)
            internal fun String.parseToResolvableString(
                expressionPrefix: String = "\${{",
                expressionSuffix: String = "}}",
            ): ResolvableString {
                // Match prefix ... suffix blocks
                val regex = Regex("""\Q$expressionPrefix\E(.*?)\Q$expressionSuffix\E""")
                val foundBlocks = mutableListOf<String>()
                val replaced =
                    regex.replace(this) {
                        val block = it.groupValues[1].trim()
                        // Note: collapse identical expressions to the same index
                        val index =
                            foundBlocks.indexOf(block).takeIf { it != -1 }.run {
                                foundBlocks.add(block)
                                foundBlocks.size - 1
                            }
                        "{{$index}}"
                    }
                return ResolvableString(replaced, foundBlocks.map { RawExpression(it) })
            }
        }
    }
