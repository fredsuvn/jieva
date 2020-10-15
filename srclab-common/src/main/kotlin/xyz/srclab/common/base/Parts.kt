@file:JvmName("Parts")
@file:JvmMultifileClass

package xyz.srclab.common.base

import com.google.common.base.CharMatcher

const val UNINITIALIZED_VALUE = "UNINITIALIZED_VALUE"

val DOT_MATCHER = CharMatcher.`is`('.')

val HYPHEN_MATCHER = CharMatcher.`is`('-')

val PLUS_SIGN_MATCHER = CharMatcher.`is`('+')