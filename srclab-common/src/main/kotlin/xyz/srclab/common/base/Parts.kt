package xyz.srclab.common.base

import com.google.common.base.CharMatcher

object Parts {

    const val UNINITIALIZED_VALUE = "UNINITIALIZED_VALUE"

    const val NULL_VALUE = "NULL_VALUE"

    private val DOT_MATCHER = CharMatcher.`is`('.')
    private val HYPHEN_MATCHER = CharMatcher.`is`('-')
    private val PLUS_SIGN_MATCHER = CharMatcher.`is`('+')

    @JvmStatic
    fun dotMatcher(): CharMatcher = DOT_MATCHER

    @JvmStatic
    fun hyphenMatcher(): CharMatcher = HYPHEN_MATCHER

    @JvmStatic
    fun plusSignMatcher(): CharMatcher = PLUS_SIGN_MATCHER
}