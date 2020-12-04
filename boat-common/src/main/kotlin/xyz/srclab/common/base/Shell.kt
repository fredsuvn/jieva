package xyz.srclab.common.base

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.util.*

interface Shell {

    fun print(any: Any?)

    fun println(any: Any?)

    @JvmDefault
    fun plainText(value: CharSequence): ShellText {
        return plainText(false, value)
    }

    @JvmDefault
    fun plainText(isControl: Boolean, value: CharSequence): ShellText {
        return PlainText(isControl, value.toString())
    }

    @JvmDefault
    fun concatText(vararg shellTexts: ShellText): ShellText {
        return concatText(shellTexts.toList())
    }

    @JvmDefault
    fun concatText(shellTexts: List<ShellText>): ShellText {
        return MultiText(shellTexts)
    }

    fun resetText(): ShellText

    fun cursorUpText(n: Int = 1): ShellText

    fun cursorDownText(n: Int = 1): ShellText

    fun cursorForwardText(n: Int = 1): ShellText

    fun cursorBackText(n: Int = 1): ShellText

    fun cursorNextLineText(n: Int = 1): ShellText

    fun cursorPreviousLineText(n: Int = 1): ShellText

    fun cursorHorizontalAbsoluteText(n: Int = 1): ShellText

    fun cursorPositionText(n: Int, m: Int): ShellText

    fun eraseText(n: Int = 2): ShellText

    fun eraseLineText(n: Int = 2): ShellText

    fun scrollUpText(n: Int = 1): ShellText

    fun scrollDownText(n: Int = 1): ShellText

    fun reportCursorPositionText(): ShellText

    fun saveCursorPositionText(): ShellText

    fun restoreCursorPositionText(): ShellText

    fun richText(param: CharSequence): ShellText {
        return richText(listOf(param))
    }

    fun richText(vararg params: CharSequence): ShellText {
        return richText(params.toList())
    }

    fun richText(params: List<CharSequence>): ShellText

    fun read(): String

    fun readLine(): String

    @JvmDefault
    fun echo(text: CharSequence) {
        val process = Runtime.getRuntime().exec("echo $text")
        val inputStream = process.inputStream
        val inputStreamReader = InputStreamReader(inputStream)
        val inputReader = BufferedReader(inputStreamReader)
        val input = inputReader.readText()
        inputReader.close()
        print(input)
        val errorStream = process.errorStream
        val errorStreamReader = InputStreamReader(errorStream)
        val errorReader = BufferedReader(errorStreamReader)
        val error = errorReader.readText()
        errorReader.close()
        print(error)
    }

    companion object {

        @JvmField
        val DEFAULT: Shell = DefaultShell

        private class PlainText(override val isControl: Boolean, override val value: String) : ShellText

        private class MultiText(shellTexts: List<ShellText>) : ShellText {
            override val isControl: Boolean = false
            override val value: String by lazy { shellTexts.joinToString(separator = "") { t -> t.value } }
        }
    }
}

interface ShellText {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isControl: Boolean
        @JvmName("isControl") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val value: String
        @JvmName("value") get
}










interface UnixRichTextParam {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val value: String
        @JvmName("value") get

    @JvmDefault
    fun toText(): String {
        return "\\033[${value}m"
    }

    @JvmDefault
    fun toText(plain: CharSequence): String {
        return "${toText()}$plain${RESET.toText()}"
    }

    companion object {

        @JvmField
        val RESET: UnixRichTextParam = of("0")

        @JvmField
        val BOLD: UnixRichTextParam = of("1")

        @JvmField
        val HALF_BRIGHT: UnixRichTextParam = of("2")

        @JvmField
        val ITALIC: UnixRichTextParam = of("3")

        @JvmField
        val UNDERSCORE: UnixRichTextParam = of("4")

        @JvmField
        val BLINK: UnixRichTextParam = of("5")

        @JvmField
        val FAST_BLINK: UnixRichTextParam = of("6")

        @JvmField
        val INVERSE: UnixRichTextParam = of("7")

        @JvmField
        val INVISIBLE: UnixRichTextParam = of("8")

        @JvmField
        val STRIKETHROUGH: UnixRichTextParam = of("9")

        @JvmField
        val PRIMARY_FONT: UnixRichTextParam = of("10")

        @JvmField
        val ALTERNATE_FONT_1: UnixRichTextParam = of("11")

        @JvmField
        val ALTERNATE_FONT_2: UnixRichTextParam = of("12")

        @JvmField
        val ALTERNATE_FONT_3: UnixRichTextParam = of("13")

        @JvmField
        val ALTERNATE_FONT_4: UnixRichTextParam = of("14")

        @JvmField
        val ALTERNATE_FONT_5: UnixRichTextParam = of("15")

        @JvmField
        val ALTERNATE_FONT_6: UnixRichTextParam = of("16")

        @JvmField
        val ALTERNATE_FONT_7: UnixRichTextParam = of("17")

        @JvmField
        val ALTERNATE_FONT_8: UnixRichTextParam = of("18")

        @JvmField
        val ALTERNATE_FONT_9: UnixRichTextParam = of("19")

        @JvmField
        val BOLD_OFF: UnixRichTextParam = of("21")

        @JvmField
        val HALF_BRIGHT_OFF: UnixRichTextParam = of("22")

        @JvmField
        val ITALIC_OFF: UnixRichTextParam = of("23")

        @JvmField
        val UNDERSCORE_OFF: UnixRichTextParam = of("24")

        @JvmField
        val BLINK_OFF: UnixRichTextParam = of("25")

        @JvmField
        val FAST_BLINK_OFF: UnixRichTextParam = of("26")

        @JvmField
        val INVERSE_OFF: UnixRichTextParam = of("27")

        @JvmField
        val INVISIBLE_OFF: UnixRichTextParam = of("28")

        @JvmField
        val STRIKETHROUGH_OFF: UnixRichTextParam = of("29")

        @JvmField
        val BLACK_FOREGROUND: UnixRichTextParam = of("30")

        @JvmField
        val RED_FOREGROUND: UnixRichTextParam = of("31")

        @JvmField
        val GREEN_FOREGROUND: UnixRichTextParam = of("32")

        @JvmField
        val BROWN_FOREGROUND: UnixRichTextParam = of("33")

        @JvmField
        val BLUE_FOREGROUND: UnixRichTextParam = of("34")

        @JvmField
        val MAGENTA_FOREGROUND: UnixRichTextParam = of("35")

        @JvmField
        val CYAN_FOREGROUND: UnixRichTextParam = of("36")

        @JvmField
        val WHITE_FOREGROUND: UnixRichTextParam = of("37")

        @JvmField
        val DEFAULT_FOREGROUND: UnixRichTextParam = of("39")

        @JvmField
        val BLACK_BACKGROUND: UnixRichTextParam = of("40")

        @JvmField
        val RED_BACKGROUND: UnixRichTextParam = of("41")

        @JvmField
        val GREEN_BACKGROUND: UnixRichTextParam = of("42")

        @JvmField
        val BROWN_BACKGROUND: UnixRichTextParam = of("43")

        @JvmField
        val BLUE_BACKGROUND: UnixRichTextParam = of("44")

        @JvmField
        val MAGENTA_BACKGROUND: UnixRichTextParam = of("45")

        @JvmField
        val CYAN_BACKGROUND: UnixRichTextParam = of("46")

        @JvmField
        val WHITE_BACKGROUND: UnixRichTextParam = of("47")

        @JvmField
        val DEFAULT_BACKGROUND: UnixRichTextParam = of("49")

        @JvmField
        val FRAMED: UnixRichTextParam = of("51")

        @JvmField
        val ENCIRCLED: UnixRichTextParam = of("52")

        @JvmField
        val OVERLINE: UnixRichTextParam = of("53")

        @JvmField
        val FRAMED_ENCIRCLED_OFF: UnixRichTextParam = of("54")

        @JvmField
        val OVERLINE_OFF: UnixRichTextParam = of("55")

        @JvmField
        val BRIGHT_BLACK_FOREGROUND: UnixRichTextParam = of("90")

        @JvmField
        val BRIGHT_RED_FOREGROUND: UnixRichTextParam = of("91")

        @JvmField
        val BRIGHT_GREEN_FOREGROUND: UnixRichTextParam = of("92")

        @JvmField
        val BRIGHT_BROWN_FOREGROUND: UnixRichTextParam = of("93")

        @JvmField
        val BRIGHT_BLUE_FOREGROUND: UnixRichTextParam = of("94")

        @JvmField
        val BRIGHT_MAGENTA_FOREGROUND: UnixRichTextParam = of("95")

        @JvmField
        val BRIGHT_CYAN_FOREGROUND: UnixRichTextParam = of("96")

        @JvmField
        val BRIGHT_WHITE_FOREGROUND: UnixRichTextParam = of("97")

        @JvmField
        val BRIGHT_BLACK_BACKGROUND: UnixRichTextParam = of("100")

        @JvmField
        val BRIGHT_RED_BACKGROUND: UnixRichTextParam = of("101")

        @JvmField
        val BRIGHT_GREEN_BACKGROUND: UnixRichTextParam = of("102")

        @JvmField
        val BRIGHT_BROWN_BACKGROUND: UnixRichTextParam = of("103")

        @JvmField
        val BRIGHT_BLUE_BACKGROUND: UnixRichTextParam = of("104")

        @JvmField
        val BRIGHT_MAGENTA_BACKGROUND: UnixRichTextParam = of("105")

        @JvmField
        val BRIGHT_CYAN_BACKGROUND: UnixRichTextParam = of("106")

        @JvmField
        val BRIGHT_WHITE_BACKGROUND: UnixRichTextParam = of("107")

        @JvmStatic
        fun of(value: CharSequence): UnixRichTextParam {
            return UnixRichTextParamImpl(value.toString())
        }

        @JvmStatic
        fun concat(vararg richTextParams: UnixRichTextParam): UnixRichTextParam {
            return concat(richTextParams.toList())
        }

        @JvmStatic
        fun concat(richTextParams: List<UnixRichTextParam>): UnixRichTextParam {
            return UnixRichTextParamImpl(richTextParams.joinToString(separator = ";") { r -> r.value })
        }

        @JvmStatic
        fun alternateFont(n: Int): UnixRichTextParam {
            return when (n) {
                1 -> ALTERNATE_FONT_1
                2 -> ALTERNATE_FONT_2
                3 -> ALTERNATE_FONT_3
                4 -> ALTERNATE_FONT_4
                5 -> ALTERNATE_FONT_5
                6 -> ALTERNATE_FONT_6
                7 -> ALTERNATE_FONT_7
                8 -> ALTERNATE_FONT_8
                9 -> ALTERNATE_FONT_9
                else -> throw IllegalArgumentException("Number of Alternate Font should be in 1..10.")
            }
        }

        @JvmStatic
        fun colorForeground(n: Int): UnixRichTextParam {
            return UnixRichTextParamImpl("38;5;$n")
        }

        @JvmStatic
        fun colorForeground(r: Int, g: Int, b: Int): UnixRichTextParam {
            return UnixRichTextParamImpl("38;2;$r;$g;$b")
        }

        @JvmStatic
        fun colorBackground(n: Int): UnixRichTextParam {
            return UnixRichTextParamImpl("48;5;$n")
        }

        @JvmStatic
        fun colorBackground(r: Int, g: Int, b: Int): UnixRichTextParam {
            return UnixRichTextParamImpl("48;2;$r;$g;$b")
        }

        private class UnixRichTextParamImpl(override val value: String) : UnixRichTextParam
    }
}

interface EscText {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val value: String
        @JvmName("value") get

    override fun toString(): String

    companion object {

        private val RESET: EscText = of("c")

        @JvmStatic
        fun reset(): EscText {
            return RESET
        }

        @JvmStatic
        fun of(value: String): EscText {
            return EscTextImpl(value)
        }
    }
}

private open class EscTextImpl(override val value: String) : EscText {
    override fun toString(): String {
        return "\\033$value"
    }
}

interface CsiText : EscText {

    companion object {

        private val REPORT_CURSOR_POSITION: CsiText = of("6n")
        private val SAVE_CURSOR_POSITION: CsiText = of("s")
        private val RESTORE_CURSOR_POSITION: CsiText = of("u")

        @JvmStatic
        @JvmOverloads
        fun cursorUp(n: Int = 1): CsiText {
            return of("${n}A")
        }

        @JvmStatic
        @JvmOverloads
        fun cursorDown(n: Int = 1): CsiText {
            return of("${n}B")
        }

        @JvmStatic
        @JvmOverloads
        fun cursorForward(n: Int = 1): CsiText {
            return of("${n}C")
        }

        @JvmStatic
        @JvmOverloads
        fun cursorBack(n: Int = 1): CsiText {
            return of("${n}D")
        }

        @JvmStatic
        @JvmOverloads
        fun cursorNextLine(n: Int = 1): CsiText {
            return of("${n}E")
        }

        @JvmStatic
        @JvmOverloads
        fun cursorPreviousLine(n: Int = 1): CsiText {
            return of("${n}F")
        }

        @JvmStatic
        @JvmOverloads
        fun cursorHorizontalAbsolute(n: Int = 1): CsiText {
            return of("${n}G")
        }

        @JvmStatic
        fun cursorPosition(n: Int, m: Int): CsiText {
            return of("${n};${m}H")
        }

        @JvmStatic
        @JvmOverloads
        fun erase(n: Int = 2): CsiText {
            return of("${n}J")
        }

        @JvmStatic
        @JvmOverloads
        fun eraseLine(n: Int = 2): CsiText {
            return of("${n}K")
        }

        @JvmStatic
        @JvmOverloads
        fun scrollUp(n: Int = 1): CsiText {
            return of("${n}S")
        }

        @JvmStatic
        @JvmOverloads
        fun scrollDown(n: Int = 1): CsiText {
            return of("${n}T")
        }

        @JvmStatic
        fun reportCursorPosition(): CsiText {
            return REPORT_CURSOR_POSITION
        }

        @JvmStatic
        fun saveCursorPosition(): CsiText {
            return SAVE_CURSOR_POSITION
        }

        @JvmStatic
        fun restoreCursorPosition(): CsiText {
            return RESTORE_CURSOR_POSITION
        }

        @JvmStatic
        fun of(value: String): CsiText {
            return CsiTextImpl(value)
        }
    }
}

private open class CsiTextImpl(override val value: String) : EscTextImpl("[$value"), CsiText

interface SgrText : CsiText {

    companion object {

        @JvmStatic
        fun of(params: List<Param>): SgrText {
            return SgrTextImpl(params)
        }
    }

    enum class Param(
        @get:JvmName("value") val value: String,
    ) {
        RED("")
    }
}

private class SgrTextImpl(params: List<SgrText.Param>) : CsiTextImpl(
    params.joinToString(separator = ";") { p -> p.value } + "m"
), SgrText

abstract class StreamShell(input: InputStream, output: PrintStream) : Shell {

    private val scanner = Scanner(input)
    private val printStream = output

    override fun print(any: Any?) {
        printStream.print(any)
    }

    override fun println(any: Any?) {
        printStream.println(any)
    }

    override fun read(): String {
        return scanner.next()
    }

    override fun readLine(): String {
        return scanner.nextLine()
    }
}

object DefaultShell : StreamShell(System.`in`, System.out)