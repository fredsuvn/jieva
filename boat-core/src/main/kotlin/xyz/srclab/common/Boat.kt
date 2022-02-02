package xyz.srclab.common

import xyz.srclab.common.base.loadResourceProperties
import xyz.srclab.common.utils.About
import xyz.srclab.common.utils.Author
import xyz.srclab.common.utils.SemVer
import xyz.srclab.common.utils.SemVer.Companion.parseSemVer

object Boat {

    private val buildInfos: Map<String, String> = "META-INF/build.properties".loadResourceProperties()

    private val sunqian = Author(
        "Sun Qian",
        "fredsuvn@163.com",
        "https://github.com/fredsuvn"
    )

    private val srclab = Author(
        "SrcLab",
        "srclab@163.com",
        "https://github.com/srclab-projects"
    )

    @JvmField
    val VERSION: SemVer = buildInfos["build.version"]!!.parseSemVer()

    @JvmField
    val SERIAL_VERSION: Long = buildInfos["build.serial.version"]!!.toLong()

    @JvmField
    val ABOUT: About = About(
        "Boat",
        VERSION.toString(),
        listOf(sunqian, srclab),
        srclab.mail,
        "https://github.com/srclab-projects/boat",
        listOf("Apache 2.0 license"),
        emptyList(),
        "© 2021 SrcLab"
    )

    @JvmField
    val SECRET_CODES: List<String> = listOf(
        "Thank you, Taro.",
        "谢谢你，泰罗。",
    )
}