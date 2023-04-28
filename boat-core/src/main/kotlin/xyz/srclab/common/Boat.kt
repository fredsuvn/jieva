package xyz.srclab.common

import xyz.srclab.common.base.loadProperties

object Boat {

    private val buildInfos: Map<String, String> = loadProperties("META-INF/build.properties")!!
    private val serialVersion: Long = buildInfos["build.serial.version"]!!.toLong()
    private val version: String = buildInfos["build.version"]!!

    /**
     * Returns name of this lib.
     */
    @JvmStatic
    fun name(): String = "Boat"

    /**
     * Returns current version of boat.
     */
    @JvmStatic
    fun version(): String = version

    /**
     * Returns serial version for current boat version.
     */
    @JvmStatic
    fun serialVersion(): Long = serialVersion

    /**
     * Returns url of boat.
     */
    @JvmStatic
    fun url(): String = "https://github.com/srclab-projects/boat"

    /**
     * Returns author of boat.
     */
    @JvmStatic
    fun author(): String = "孙谦"

    /**
     * Returns author mail of boat.
     */
    @JvmStatic
    fun authorMail(): String = "fredsuvn@163.com"

    /**
     * Returns author home of boat.
     */
    @JvmStatic
    fun authorHome(): String = "https://github.com/fredsuvn"

    /**
     * Returns team of boat.
     */
    @JvmStatic
    fun team(): String = "SrcLab"

    /**
     * Returns team mail of boat.
     */
    @JvmStatic
    fun teamMail(): String = "srclab@163.com"

    /**
     * Returns team home of boat.
     */
    @JvmStatic
    fun teamHome(): String = "https://github.com/srclab-projects"

    /**
     * Returns join us of boat.
     */
    @JvmStatic
    fun joinUs(): String = "https://github.com/srclab-projects"

    @JvmStatic
    fun license(): String = "https://www.apache.org/licenses/LICENSE-2.0"

    @JvmStatic
    fun copyright(): String = "© 2019-9999 SrcLab"

    /**
     * WOW!
     */
    fun secretCodes(): List<String> = listOf(
        "Thank you, Taro.",
        "谢谢你，泰罗。",
    )
}