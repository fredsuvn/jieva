@file:JvmName("Parts")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher

const val UNINITIALIZED_VALUE = "UNINITIALIZED_VALUE"

const val NULL_VALUE = "NULL_VALUE"

const val ABSENT_VALUE = "ABSENT_VALUE"

const val INAPPLICABLE_JVM_NAME = "INAPPLICABLE_JVM_NAME"

@JvmField
val DOT_MATCHER = CharMatcher.`is`('.')

@JvmField
val HYPHEN_MATCHER = CharMatcher.`is`('-')

@JvmField
val PLUS_SIGN_MATCHER = CharMatcher.`is`('+')