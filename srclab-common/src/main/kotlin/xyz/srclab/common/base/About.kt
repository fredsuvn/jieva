package xyz.srclab.common.base

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author sunqian
 */
interface About {

    fun name(): String

    fun version(): String

    fun releaseDate(): ZonedDateTime

    fun license(): String

    fun poweredBy(): String

    companion object {

        @JvmStatic
        fun current(): About = V0_0

        private abstract class BaseAbout : About {

            override fun name(): String = "srclab-common"

            override fun license(): String = "Apache 2.0 license [https://www.apache.org/licenses/LICENSE-2.0.html]"

            override fun poweredBy(): String = "srclab.xyz"
        }

        private object V0_0 : BaseAbout() {

            override fun version(): String = "0.0.0"

            override fun releaseDate(): ZonedDateTime = ZonedDateTime.of(
                LocalDate.of(2020, 1, 1),
                LocalTime.MIN,
                ZoneId.of("Asia/Shanghai")
            )
        }
    }
}