package xyz.srclab.common

import xyz.srclab.common.base.loadResourceProperties
import xyz.srclab.common.utils.About
import xyz.srclab.common.utils.Author
import xyz.srclab.common.utils.SemVer
import xyz.srclab.common.utils.SemVer.Companion.parseSemVer

object Boat {

    private val buildInfos: Map<String, String> = "META-INF/build.properties".loadResourceProperties()
    private val serialVersion: Long = buildInfos["build.serial.version"]!!.toLong()
    private val version: SemVer = buildInfos["build.version"]!!.parseSemVer()

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

    private val about: About = About(
        "Boat",
        version.toString(),
        listOf(sunqian, srclab),
        srclab.mail,
        "https://github.com/srclab-projects/boat",
        listOf("Apache 2.0 license"),
        emptyList(),
        "© 2021 SrcLab"
    )

    /**
     * Returns name of this lib.
     */
    @JvmStatic
    fun name(): String = "Boat"

    /**
     * Returns current version of boat.
     */
    @JvmStatic
    fun version(): SemVer = version

    /**
     * Returns about info of boat.
     */
    @JvmStatic
    fun about(): About = about

    /**
     * Returns serial version for current boat version.
     */
    @JvmStatic
    fun serialVersion(): Long = serialVersion

    private val secretCodes: List<String> = listOf(
        "Thank you, Taro.",
        "谢谢你，泰罗。",
    )

    /**
     * WOW!
     */
    fun secretCodes(): List<String> = secretCodes
}