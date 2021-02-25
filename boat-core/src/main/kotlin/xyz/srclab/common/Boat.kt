package xyz.srclab.common

import xyz.srclab.common.base.About
import xyz.srclab.common.base.Author
import xyz.srclab.common.base.SemVer

object Boat {

    private val sunqian = Author.of("Sun Qian", "fredsuvn@163.com", null)
    private val srclab = Author.of("SrcLab", "srclab@163.com", "https://github.com/srclab-projects")

    @JvmStatic
    @get:JvmName("semVer")
    val version: SemVer = SemVer.of(0, 1, 0)

    @JvmStatic
    @get:JvmName("about")
    val about = About.of(
        "Boat",
        version.toString(),
        listOf(sunqian, srclab),
        srclab.mail,
        "https://github.com/srclab-projects/boat",
        listOf("Apache 2.0 license"),
        emptyList(),
        "© 2021 SrcLab"
    )

    /*
     * ________                _____
     * ___  __ )______ ______ ___  /_
     * __  __  |_  __ \_  __ `/_  __/
     * _  /_/ / / /_/ // /_/ / / /_
     * /_____/  \____/ \__,_/  \__/
     *
     * Source: http://www.network-science.de/ascii/, http://patorjk.com/software/taag/
     * Font: speed
     */
    @JvmStatic
    @get:JvmName("logo")
    val logo = """
        _______                                      _____________________________
         _________________________________________________________________
          _____  ________                _____     ________________
           ____  ___  __ )______ ______ ___  /_   ___________
            ___  __  __  |_  __ \_  __ `/_  __/  _______
             __  _  /_/ / / /_/ // /_/ / / /_   ____
              _  /_____/  \____/ \__,_/  \__/  __
               ________________________________
        :: Boat ::                                        v$version
    """.trimIndent()
}