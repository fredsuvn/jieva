package xyz.srclab.common

import xyz.srclab.common.base.*
import java.time.ZonedDateTime

object AboutBoat : About {
    override val name: String
        get() = TODO("Not yet implemented")
    override val version: String
        get() = semVer.toString()
    override val author: String
        get() = TODO("Not yet implemented")
    override val url: String
        get() = TODO("Not yet implemented")
    override val licence: String
        get() = TODO("Not yet implemented")
    override val poweredBy: PoweredBy
        get() = TODO("Not yet implemented")
    override val copyright: String
        get() = TODO("Not yet implemented")

    val semVer: SemVer = SemVer.of(0, 1, 1)
}

@JvmOverloads
fun aboutBoat(major: Int = currentMajor): About {
    return when (major) {
        0 -> aboutBoatV0
        1 -> aboutBoatV1
        else -> throw IllegalArgumentException("Version of major not found: $major")
    }
}

private const val currentMajor = 0

private const val nameFromV0 = "Boat"

private const val urlFromV0 = "https://github.com/srclab-projects/boat"

private val poweredBySrcLab = PoweredBy.of(
    "SrcLab",
    "https://github.com/srclab-projects",
    "fredsuvn@163.com"
)

private val aboutBoatV0 = About.of(
    nameFromV0,
    urlFromV0,
    Version.of(
        ZonedDateTime.of(
            2020, 10, 17,
            23, 59, 59, 0,
            zoneIdFromV0
        ),
        0, 0, 0
    ),
    licenceOfApache2,
    poweredBySrcLab,
    """
        直长多行忽闲将欲拔停玉金
        挂风歧路复来登渡剑杯盘樽
        云破路难乘垂太黄四投珍清
        帆浪，，舟钓行河顾箸羞酒
        济会今行梦碧雪冰心不直斗
        沧有安路日溪满塞茫能万十
        海时在难边上山川然食钱千
    """.trimIndent()
)

private val aboutBoatV1 = About.of(
    nameFromV0,
    urlFromV0,
    Version.of(
        ZonedDateTime.of(
            2020, 11, 11,
            23, 59, 59, 0,
            zoneIdFromV0
        ),
        1, 0, 0, listOf("pre-plan")
    ),
    licenceOfApache2,
    poweredBySrcLab,
    """
        君不见，黄河之水天上来，奔流到海不复回。
        君不见，高堂明镜悲白发，朝如青丝暮成雪。
        人生得意须尽欢，莫使金樽空对月。
        天生我材必有用，千金散尽还复来。
        烹羊宰牛且为乐，会须一饮三百杯。
        岑夫子，丹丘生，将进酒，杯莫停。
        与君歌一曲，请君为我倾耳听。
        钟鼓馔玉不足贵，但愿长醉不愿醒。
        古来圣贤皆寂寞，惟有饮者留其名。
        陈王昔时宴平乐，斗酒十千恣欢谑。
        主人何为言少钱，径须沽取对君酌。
        五花马、千金裘，呼儿将出换美酒，与尔同销万古愁。
    """.trimIndent()
)

interface Egg {

    fun start()
}

class EggNotFoundException : RuntimeException("You asked me for an egg? Now I give you a bullshit!")

@JvmOverloads
fun giveMeAnEgg(major: Int = currentMajor): Egg {
    return when (major) {
        0 -> ALoneBoatWithAStrawDressedOldFisherInTheSnow
        1 -> Starmon
        else -> throw EggNotFoundException()
    }
}

private object ALoneBoatWithAStrawDressedOldFisherInTheSnow : Egg {
    override fun start() {
        TODO("Not yet implemented")
    }
}

private object Starmon : Egg {
    override fun start() {
        TODO("Not yet implemented")
    }
}

/*
 * __________                            ____________              _____
 * ___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________
 * __  __/  _  __ `/_  ___/_  / / /___________ \_  __/  __ `/_  ___/  __/  _ \_  ___/
 * _  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /
 * /_____/  \__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
 *                        /____/
 *                                                                srclab.xyz, sunqian
 *                                                                   fredsuvn@163.com
 *
 * Source: http://www.network-science.de/ascii/
 * Font: speed
 */
private val logo = """
 __________                            ____________              _____
 ___  ____/_____ ____________  __      __  ___/_  /______ _________  /_____________
 __  __/  _  __ `/_  ___/_  / / /___________ \_  __/  __ `/_  ___/  __/  _ \_  ___/
 _  /___  / /_/ /_(__  )_  /_/ /_/_____/___/ // /_ / /_/ /_  /   / /_ /  __/  /
 /_____/  \__,_/ /____/ _\__, /        /____/ \__/ \__,_/ /_/    \__/ \___//_/
                        /____/
                                                                srclab.xyz, sunqian
                                                                   fredsuvn@163.com

 Source: http://www.network-science.de/ascii/
 Font: speed
""".trimIndent()