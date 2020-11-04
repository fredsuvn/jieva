@file:JvmName("Parts")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher

const val NULL_VALUE = "NULL_VALUE"

const val ABSENT_VALUE = "ABSENT_VALUE"

@JvmField
val DOT_MATCHER = CharMatcher.`is`('.')

@JvmField
val HYPHEN_MATCHER = CharMatcher.`is`('-')

@JvmField
val PLUS_SIGN_MATCHER = CharMatcher.`is`('+')