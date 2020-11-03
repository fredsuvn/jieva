package test.xyz.srclab.common.base

import kotlin.test.Test
import xyz.srclab.common.base.Format.Companion.fastFormat
import xyz.srclab.common.base.Format.Companion.messageFormat
import xyz.srclab.common.base.Format.Companion.printfFormat

@Test
fun testFormat() {
    println(
        "This is {} {}!".fastFormat("fast", "format")
    )
    println(
        "This is %s %s!".printfFormat("printf", "format")
    )
    println(
        "This is {0} {1}!".messageFormat("message", "format")
    )
}