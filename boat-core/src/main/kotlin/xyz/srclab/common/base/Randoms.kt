@file:JvmName("Randoms")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.util.*

@JvmOverloads
@JvmName("between")
fun randomBetween(from: Int, to: Int, random: Random = Random()): Int {
    return random.nextInt(to - from) + from
}