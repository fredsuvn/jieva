@file:JvmName("Parts")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher

@JvmField
val DOT_MATCHER = CharMatcher.`is`('.')

@JvmField
val HYPHEN_MATCHER = CharMatcher.`is`('-')

@JvmField
val PLUS_SIGN_MATCHER = CharMatcher.`is`('+')