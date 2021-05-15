package sample.kotlin.xyz.srclab.common.utils

import org.testng.annotations.Test
import xyz.srclab.common.test.TestLogger
import xyz.srclab.common.utils.About
import xyz.srclab.common.utils.Author
import xyz.srclab.common.utils.Counter.Companion.counterStarts
import xyz.srclab.common.utils.SemVer
import xyz.srclab.common.utils.SemVer.Companion.parseSemVer

class BaseSample {

    @Test
    fun testAbout() {
        val verString = "1.2.3-beta.2.3+123"
        val semVer: SemVer = verString.parseSemVer()
        val about = About.of(
            "name",
            semVer.normalString,
            listOf(Author.of("name", "author@mail.com", null)),
            "123@123.com",
            "url",
            listOf("licence"),
            listOf(
                About.of(
                    "poweredBy",
                    null,
                    emptyList(),
                    null,
                    null,
                    emptyList(),
                    emptyList(),
                    null
                )
            ),
            "© 2021 SrcLab"
        )
        //name
        //Version: 1.2.3
        //Author: name(author@mail.com)
        //Mail: 123@123.com
        //Url: url
        //Licence: licence
        //Powered by: poweredBy
        //© 2021 SrcLab
        logger.log("About: {}", about)
    }

    @Test
    fun testCounter() {
        val counter = 100.counterStarts()
        counter.getAndIncrementInt()
        counter.reset()
        val atomicCounter = 100.counterStarts(true)
        atomicCounter.incrementAndGetInt()
        atomicCounter.reset()
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}