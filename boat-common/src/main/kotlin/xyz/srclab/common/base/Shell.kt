package xyz.srclab.common.base

import java.io.PrintStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

interface Shell : ShellIO {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val errorOutput: PrintStream
        @JvmName("errorOutput") get

    @JvmDefault
    fun print(chars: ShellChars) {
        print(chars.value)
    }

    @JvmDefault
    fun print(vararg chars: ShellChars) {
        for (c in chars) {
            print(c)
        }
    }

    @JvmDefault
    fun println(chars: ShellChars) {
        println(chars.value)
    }

    @JvmDefault
    fun println(vararg chars: ShellChars) {
        print(*chars)
        println()
    }

    @JvmDefault
    fun print(chars: List<Any>) {
        for (c in chars) {
            if (c is ShellChars) {
                print(c)
            } else {
                print(c.toString())
            }
        }
    }

    @JvmDefault
    fun println(chars: List<Any>) {
        print(chars)
        println()
    }

    @JvmDefault
    fun run(vararg command: CharSequence): ShellProcess {
        return ShellProcess.of(
            ProcessBuilder()
                .command(*command.toStringArray())
                .redirectErrorStream(true)
                .start(),
            charset
        )
    }

    @JvmDefault
    fun run(command: List<CharSequence>): ShellProcess {
        return ShellProcess.of(
            ProcessBuilder()
                .command(command.map { it.toString() })
                .redirectErrorStream(true)
                .start(),
            charset
        )
    }

    companion object {

        @JvmField
        val DEFAULT: Shell = SystemShell
    }
}

interface ShellChars {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isControl: Boolean
        @JvmName("isControl") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isEscape: Boolean
        @JvmName("isEscape") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val value: String
        @JvmName("value") get

    companion object {

        @JvmStatic
        fun of(isControl: Boolean, isEscape: Boolean, value: CharSequence): ShellChars {
            return ShellCharsImpl(isControl, isEscape, value.toString())
        }

        @JvmStatic
        fun concat(vararg shellChars: ShellChars): ShellChars {
            return concat(shellChars.toList())
        }

        @JvmStatic
        fun concat(shellChars: List<ShellChars>): ShellChars {
            return ConcatShellChars(shellChars)
        }

        private class ShellCharsImpl(
            override val isControl: Boolean,
            override val isEscape: Boolean,
            override val value: String
        ) : ShellChars

        private class ConcatShellChars(shellChars: List<ShellChars>) : ShellChars {

            override val isControl: Boolean
            override val isEscape: Boolean
            override val value: String

            init {
                var ic = false
                var ie = false
                for (text in shellChars) {
                    if (text.isControl) {
                        ic = true
                        if (ie) {
                            break
                        }
                    }
                    if (text.isEscape) {
                        ie = true
                        if (ic) {
                            break
                        }
                    }
                }
                isControl = ic
                isEscape = ie
                value = shellChars.joinToString(separator = "") { text -> text.value }
            }
        }
    }
}

interface ShellProcess : ShellIO {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val process: Process
        @JvmName("process") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isAlive: Boolean
        @JvmName("isAlive") get() {
            return process.isAlive
        }

    @JvmDefault
    //@Throws(InterruptedException::class)
    fun waitFor(): Int {
        return process.waitFor()
    }

    @JvmDefault
    //@Throws(InterruptedException::class)
    fun waitFor(timeout: Duration): Boolean {
        return process.waitFor(timeout.toNanos(), TimeUnit.NANOSECONDS)
    }

    @JvmDefault
    fun exitValue(): Int {
        return process.exitValue()
    }

    @JvmDefault
    fun destroy() {
        return process.destroy()
    }

    @JvmDefault
    fun destroyForcibly(): ShellProcess {
        val p = process.destroyForcibly()
        return if (process === p) this else of(p, charset)
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(process: Process, charset: Charset = Charset.defaultCharset()): ShellProcess {
            return ShellProcessImpl(process, charset)
        }

        private class ShellProcessImpl(
            override val process: Process,
            override val charset: Charset,
        ) : ShellProcess {

            override val input: Scanner by lazy {
                Scanner(process.inputStream, charset.name())
            }

            override val output: PrintStream by lazy {
                PrintStream(process.outputStream, false, charset.name())
            }
        }
    }
}

interface ShellIO {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val charset: Charset
        @JvmName("charset") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val input: Scanner
        @JvmName("input") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val output: PrintStream
        @JvmName("output") get

    @JvmDefault
    fun read(): String {
        return input.next()
    }

    @JvmDefault
    fun readLine(): String {
        return input.nextLine()
    }

    @JvmDefault
    fun print(chars: CharSequence) {
        output.print(chars)
    }

    @JvmDefault
    fun print(vararg chars: CharSequence) {
        for (c in chars) {
            output.print(c)
        }
    }

    @JvmDefault
    fun println(chars: CharSequence) {
        output.println(chars)
    }

    @JvmDefault
    fun println(vararg chars: CharSequence) {
        print(*chars)
        println()
    }

    @JvmDefault
    fun println() {
        output.println()
    }

    @JvmDefault
    fun flushPrint() {
        output.flush()
    }
}

object SystemShell : Shell {

    override val charset: Charset = Charset.defaultCharset()

    override val input: Scanner by lazy {
        Scanner(System.`in`)
    }

    override val output: PrintStream by lazy {
        System.out
    }

    override val errorOutput: PrintStream by lazy {
        System.err
    }
}

interface ShellTextProvider {

    @JvmDefault
    fun plain(value: CharSequence): ShellChars {
        return plain(false, false, value)
    }

    @JvmDefault
    fun plain(isControl: Boolean, isEscape: Boolean, value: CharSequence): ShellChars {
        return ShellChars.of(isControl, isEscape, value.toString())
    }

    @JvmDefault
    fun concat(vararg shellChars: ShellChars): ShellChars {
        return ShellChars.concat(*shellChars)
    }

    @JvmDefault
    fun concat(shellChars: List<ShellChars>): ShellChars {
        return ShellChars.concat(shellChars)
    }

    /*
    Control characters:

    BEL (0x07, ^G) beeps;

    BS (0x08, ^H) backspaces one column (but not past the beginning of the line);

    HT  (0x09,  ^I) goes to the next tab stop or to the end of the line if there is no earlier
    tab stop;

    LF (0x0A, ^J), VT (0x0B, ^K) and FF (0x0C, ^L) all give a linefeed, and if LF/NL (new-line
    mode) is set also a carriage return;

    CR (0x0D, ^M) gives a carriage return;

    SO (0x0E, ^N) activates the G1 character set;

    SI (0x0F, ^O) activates the G0 character set;

    CAN (0x18, ^X) and SUB (0x1A, ^Z) interrupt escape sequences;

    ESC (0x1B, ^[) starts an escape sequence;

    DEL (0x7F) is ignored;

    CSI (0x9B) is equivalent to ESC [.
    */

    fun beep()

    fun backspaces()

    fun goNextTab()

    fun linefeed()

    fun carriageReturn()

    fun activateG1Charset()

    fun activateG0Charset()

    fun interruptEscape()

    fun escape(value: CharSequence)

    /*
    ESC- but not CSI-sequences

    ESC c     RIS      Reset.
    ESC D     IND      Linefeed.
    ESC E     NEL      Newline.
    ESC H     HTS      Set tab stop at current column.
    ESC M     RI       Reverse linefeed.
    ESC Z     DECID    DEC private identification. The kernel returns the
                      string  ESC [ ? 6 c, claiming that it is a VT102.
    ESC 7     DECSC    Save   current    state    (cursor    coordinates,
                      attributes, character sets pointed at by G0, G1).
    ESC 8     DECRC    Restore state most recently saved by ESC 7.
    ESC [     CSI      Control sequence introducer
    ESC %              Start sequence selecting character set
    ESC % @               Select default (ISO 646 / ISO 8859-1)
    ESC % G               Select UTF-8
    ESC % 8               Select UTF-8 (obsolete)
    ESC # 8   DECALN   DEC screen alignment test - fill screen with E's.
    ESC (              Start sequence defining G0 character set
    ESC ( B               Select default (ISO 8859-1 mapping)
    ESC ( 0               Select VT100 graphics mapping
    ESC ( U               Select null mapping - straight to character ROM
    ESC ( K               Select user mapping - the map that is loaded by
                         the utility mapscrn(8).
    ESC )              Start sequence defining G1
                      (followed by one of B, 0, U, K, as above).
    ESC >     DECPNM   Set numeric keypad mode
    ESC =     DECPAM   Set application keypad mode
    ESC ]     OSC      (Should  be:  Operating  system  command)  ESC ] P
                      nrrggbb: set palette, with parameter  given  in  7
                      hexadecimal  digits after the final P :-(.  Here n
                      is the color  (0–15),  and  rrggbb  indicates  the
                      red/green/blue  values  (0–255).   ESC  ] R: reset
                      palette
     */

    fun escapeReset(): ShellChars

    fun escapeLinefeed()

    fun escapeNewline()

    fun setTabAtCurrentColumn()

    fun reverseLinefeed()

    fun saveCurrentState()

    fun restoreState()

    fun cursorUp(n: Int = 1): ShellChars

    fun cursorDown(n: Int = 1): ShellChars

    fun cursorForward(n: Int = 1): ShellChars

    fun cursorBack(n: Int = 1): ShellChars

    fun cursorNextLine(n: Int = 1): ShellChars

    fun cursorPreviousLine(n: Int = 1): ShellChars

    fun cursorHorizontalAbsolute(n: Int = 1): ShellChars

    fun cursorPosition(n: Int, m: Int): ShellChars

    fun erase(n: Int = 2): ShellChars

    fun eraseLine(n: Int = 2): ShellChars

    fun scrollUp(n: Int = 1): ShellChars

    fun scrollDown(n: Int = 1): ShellChars

    fun reportCursorPosition(): ShellChars

    fun saveCursorPosition(): ShellChars

    fun restoreCursorPosition(): ShellChars

    fun richText(param: CharSequence): ShellChars {
        return richText(listOf(param))
    }

    fun richText(vararg params: CharSequence): ShellChars {
        return richText(params.toList())
    }

    fun richText(params: List<CharSequence>): ShellChars
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