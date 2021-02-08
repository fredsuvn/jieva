package xyz.srclab.common.base

import org.apache.commons.io.IOUtils
import xyz.srclab.common.base.ShellProcess.Companion.asShellProcess
import xyz.srclab.common.collect.arrayToStringArray
import java.io.InputStream
import java.io.PrintStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Shell to run commands.
 *
 * @see SystemShell
 * @see ShellProcess
 * @see ShellIO
 *
 * @see ControlChars
 * @see EscapeChars
 * @see CsiChars
 * @see SgrChars
 * @see SgrParam
 */
interface Shell : ShellIO {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val errorOutput: PrintStream
        @JvmName("errorOutput") get

    @JvmDefault
    fun run(vararg command: CharSequence): ShellProcess {
        return run(charset, *command)
    }

    @JvmDefault
    fun run(command: List<CharSequence>): ShellProcess {
        return run(charset, command)
    }

    @JvmDefault
    fun run(charset: Charset, vararg command: CharSequence): ShellProcess {
        return ProcessBuilder()
            .command(*command.arrayToStringArray())
            .redirectErrorStream(true)
            .start()
            .asShellProcess(charset)
    }

    @JvmDefault
    fun run(charset: Charset, command: List<CharSequence>): ShellProcess {
        return ProcessBuilder()
            .command(command.map { it.toString() })
            .redirectErrorStream(true)
            .start()
            .asShellProcess(charset)
    }

    companion object {

        @JvmField
        val DEFAULT: Shell = SystemShell()

        @JvmStatic
        fun withCharset(charset: Charset): Shell {
            return SystemShell(charset)
        }
    }
}

/**
 * Represents process called by [Shell]
 */
interface ShellProcess : ShellIO {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val process: Process
        @JvmName("process") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isAlive: Boolean
        @JvmName("isAlive") get() {
            return process.isAlive
        }

    /**
     * @throws InterruptedException
     */
    @JvmDefault
    //@Throws(InterruptedException::class)
    fun waitFor(): Int {
        return process.waitFor()
    }

    /**
     * @throws InterruptedException
     */
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
    fun close() {
        process.destroyForcibly()
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        @JvmName("forProcess")
        fun Process.asShellProcess(charset: Charset = Charset.defaultCharset()): ShellProcess {
            return ShellProcessImpl(this, charset)
        }

        private class ShellProcessImpl(
            override val process: Process,
            override val charset: Charset,
        ) : ShellProcess {

            override val input: InputStream by lazy {
                process.inputStream
            }

            override val output: PrintStream by lazy {
                PrintStream(process.outputStream, false, charset.name())
            }

            override val scanner: Scanner by lazy {
                Scanner(input, charset.name())
            }
        }
    }
}

/**
 * IO for shell operation.
 *
 * @see Shell
 * @see ShellProcess
 */
interface ShellIO {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val charset: Charset
        @JvmName("charset") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val input: InputStream
        @JvmName("input") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val output: PrintStream
        @JvmName("output") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val scanner: Scanner
        @JvmName("scanner") get

    @JvmDefault
    fun readLine(): String {
        return scanner.nextLine()
    }

    @JvmDefault
    fun readAll(): String {
        return IOUtils.toString(input, charset)
    }

    @JvmDefault
    fun print(chars: Any?) {
        output.print(chars)
    }

    @JvmDefault
    fun print(vararg chars: Any?) {
        for (c in chars) {
            output.print(c)
        }
    }

    @JvmDefault
    fun print(chars: List<Any?>) {
        for (c in chars) {
            output.print(c)
        }
    }

    @JvmDefault
    fun println(chars: Any?) {
        output.println(chars)
    }

    @JvmDefault
    fun println(vararg chars: Any?) {
        print(*chars)
        println()
    }

    @JvmDefault
    fun println(chars: List<Any?>) {
        print(chars)
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

class SystemShell(override val charset: Charset = Charset.defaultCharset()) : Shell {

    override val input: InputStream = System.`in`

    override val output: PrintStream by lazy {
        PrintStream(System.out, false, charset.name())
    }

    override val scanner: Scanner by lazy {
        Scanner(input, charset.name())
    }

    override val errorOutput: PrintStream by lazy {
        PrintStream(System.err, false, charset.name())
    }
}

/**
 * Control characters:
 *
 * * BEL (0x07, ^G) beeps;
 * * BS (0x08, ^H) backspaces one column (but not past the beginning of the line);
 * * HT  (0x09,  ^I) goes to the next tab stop or to the end of the line if there is no earlier
 * tab stop;
 * * LF (0x0A, ^J), VT (0x0B, ^K) and FF (0x0C, ^L) all give a linefeed, and if LF/NL (new-line
 * mode) is set also a carriage return;
 * * CR (0x0D, ^M) gives a carriage return;
 * * SO (0x0E, ^N) activates the G1 character set;
 * * SI (0x0F, ^O) activates the G0 character set;
 * * CAN (0x18, ^X) and SUB (0x1A, ^Z) interrupt escape sequences;
 * * ESC (0x1B, ^[) starts an escape sequence;
 * * DEL (0x7F) is ignored;
 * * CSI (0x9B) is equivalent to ESC [.
 */
object ControlChars {

    @JvmStatic
    val beep: String
        @JvmName("beep") get() = "\u0007"

    @JvmStatic
    val backspaces: String
        @JvmName("backspaces") get() = "\u0008"

    @JvmStatic
    val goNextTab: String
        @JvmName("goNextTab") get() = "\u0009"

    @JvmStatic
    val linefeed: String
        @JvmName("linefeed") get() = "\u000A"

    @JvmStatic
    val carriageReturn: String
        @JvmName("carriageReturn") get() = "\u000D"

    @JvmStatic
    val activateCharsetG1: String
        @JvmName("activateCharsetG1") get() = "\u000E"

    @JvmStatic
    val activateCharsetG0: String
        @JvmName("activateCharsetG0") get() = "\u000F"

    @JvmStatic
    val interruptEscape: String
        @JvmName("interruptEscape") get() = "\u0018"

    @JvmStatic
    fun escape(value: CharSequence): String {
        return "\u001b$value"
    }
}

/**
 * ESC- but not CSI-sequences:
 *
 * * ESC c     RIS      Reset.
 * * ESC D     IND      Linefeed.
 * * ESC E     NEL      Newline.
 * * ESC H     HTS      Set tab stop at current column.
 * * ESC M     RI       Reverse linefeed.
 * * ESC Z     DECID    DEC private identification. The kernel returns the
 * string  ESC [ ? 6 c, claiming that it is a VT102.
 * * ESC 7     DECSC    Save   current    state    (cursor    coordinates,
 * attributes, character sets pointed at by G0, G1).
 * * ESC 8     DECRC    Restore state most recently saved by ESC 7.
 * * ESC [     CSI      Control sequence introducer
 * * ESC %              Start sequence selecting character set
 *     * ESC % @               Select default (ISO 646 / ISO 8859-1)
 *     * ESC % G               Select UTF-8
 *     * ESC % 8               Select UTF-8 (obsolete)
 * * ESC # 8   DECALN   DEC screen alignment test - fill screen with E's.
 * * ESC (              Start sequence defining G0 character set
 *     * ESC ( B               Select default (ISO 8859-1 mapping)
 *     * ESC ( 0               Select VT100 graphics mapping
 *     * ESC ( U               Select null mapping - straight to character ROM
 *     * ESC ( K               Select user mapping - the map that is loaded by
 * the utility mapscrn(8).
 * * ESC )              Start sequence defining G1
 * (followed by one of B, 0, U, K, as above).
 * * ESC >     DECPNM   Set numeric keypad mode
 * * ESC =     DECPAM   Set application keypad mode
 * * ESC ]     OSC      (Should  be:  Operating  system  command)  ESC ] P
 * nrrggbb: set palette, with parameter  given  in  7
 * hexadecimal  digits after the final P :-(.  Here n
 * is the color  (0–15),  and  rrggbb  indicates  the
 * red/green/blue  values  (0–255).   ESC  ] R: reset
 * palette
 */
object EscapeChars {

    @JvmStatic
    val reset: String
        @JvmName("reset") get() = ControlChars.escape("c")

    @JvmStatic
    val linefeed: String
        @JvmName("linefeed") get() = ControlChars.escape("D")

    @JvmStatic
    val newline: String
        @JvmName("newline") get() = ControlChars.escape("E")

    @JvmStatic
    val setTabAtCurrentColumn: String
        @JvmName("setTabAtCurrentColumn") get() = ControlChars.escape("H")

    @JvmStatic
    val reverseLinefeed: String
        @JvmName("reverseLinefeed") get() = ControlChars.escape("M")

    @JvmStatic
    val saveState: String
        @JvmName("saveState") get() = ControlChars.escape("7")

    @JvmStatic
    val restoreState: String
        @JvmName("restoreState") get() = ControlChars.escape("8")

    @JvmStatic
    val selectCharsetDefault: String
        @JvmName("selectCharsetDefault") get() = selectCharset("@")

    @JvmStatic
    val selectCharsetUtf8: String
        @JvmName("selectCharsetUtf8") get() = selectCharset("G")

    @JvmStatic
    val selectCharsetUtf8Obsolete: String
        @JvmName("selectCharsetUtf8Obsolete") get() = selectCharset("8")

    @JvmStatic
    val fillScreenWithE: String
        @JvmName("fillScreenWithE") get() = ControlChars.escape("#8")

    @JvmStatic
    val defineCharsetG0Default: String
        @JvmName("defineCharsetG0Default") get() = defineCharsetG0("B")

    @JvmStatic
    val defineCharsetG0Vt100: String
        @JvmName("defineCharsetG0Vt100") get() = defineCharsetG0("0")

    @JvmStatic
    val defineCharsetG0Rom: String
        @JvmName("defineCharsetG0Rom") get() = defineCharsetG0("U")

    @JvmStatic
    val defineCharsetG0User: String
        @JvmName("defineCharsetG0User") get() = defineCharsetG0("K")

    @JvmStatic
    val defineCharsetG1Default: String
        @JvmName("defineCharsetG1Default") get() = defineCharsetG1("B")

    @JvmStatic
    val defineCharsetG1Vt100: String
        @JvmName("defineCharsetG1Vt100") get() = defineCharsetG1("0")

    @JvmStatic
    val defineCharsetG1Rom: String
        @JvmName("defineCharsetG1Rom") get() = defineCharsetG1("U")

    @JvmStatic
    val defineCharsetG1User: String
        @JvmName("defineCharsetG1User") get() = defineCharsetG1("K")

    @JvmStatic
    val setKeypadModeNumeric: String
        @JvmName("setKeypadModeNumeric") get() = ControlChars.escape(">")

    @JvmStatic
    val setKeypadModeApplication: String
        @JvmName("setKeypadModeApplication") get() = ControlChars.escape("=")

    @JvmStatic
    fun csiChars(csiChars: CharSequence): String {
        return ControlChars.escape("[") + csiChars
    }

    @JvmStatic
    fun selectCharset(charset: CharSequence): String {
        return ControlChars.escape("%") + charset
    }

    @JvmStatic
    fun defineCharsetG0(charset: CharSequence): String {
        return ControlChars.escape("(") + charset
    }

    @JvmStatic
    fun defineCharsetG1(charset: CharSequence): String {
        return ControlChars.escape(")") + charset
    }

    @JvmStatic
    fun osCommand(command: CharSequence): String {
        return ControlChars.escape("]") + command
    }
}

/**
 * ECMA-48 CSI sequences:
 *
 * CSI (or ESC [) is followed by a sequence of  parameters,  at  most  NPAR  (16),  that  are
 * decimal  numbers  separated by semicolons.  An empty or absent parameter is taken to be 0.
 * The sequence of parameters may be preceded by a single question mark.
 *
 * However, after CSI [ (or ESC [ [) a single character is read and this entire  sequence  is
 * ignored.  (The idea is to ignore an echoed function key.)
 *
 * The action of a CSI sequence is determined by its final character.
 *
 * * @   ICH       Insert the indicated # of blank characters.
 * * A   CUU       Move cursor up the indicated # of rows.
 *
 * * B   CUD       Move cursor down the indicated # of rows.
 * * C   CUF       Move cursor right the indicated # of columns.
 * * D   CUB       Move cursor left the indicated # of columns.
 * * E   CNL       Move cursor down the indicated # of rows, to column 1.
 * * F   CPL       Move cursor up the indicated # of rows, to column 1.
 * * G   CHA       Move cursor to indicated column in current row.
 * * H   CUP       Move cursor to the indicated row, column (origin at 1,1).
 * * J   ED        Erase display (default: from cursor to end of display).
 *     * ESC [ 1 J: erase from start to cursor.
 *     * ESC [ 2 J: erase whole display.
 *     * ESC [ 3 J: erase whole display including scroll-back
 * buffer (since Linux 3.0).
 * * K   EL        Erase line (default: from cursor to end of line).
 *     * ESC [ 1 K: erase from start of line to cursor.
 *     * ESC [ 2 K: erase whole line.
 * * L   IL        Insert the indicated # of blank lines.
 * * M   DL        Delete the indicated # of lines.
 * * P   DCH       Delete the indicated # of characters on current line.
 * * X   ECH       Erase the indicated # of characters on current line.
 * * a   HPR       Move cursor right the indicated # of columns.
 * * c   DA        Answer ESC [ ? 6 c: "I am a VT102".
 * * d   VPA       Move cursor to the indicated row, current column.
 * * e   VPR       Move cursor down the indicated # of rows.
 * * f   HVP       Move cursor to the indicated row, column.
 * * g   TBC       Without parameter: clear tab stop at current position.
 *     * ESC [ 3 g: delete all tab stops.
 * * h   SM        Set Mode (see below).
 * * l   RM        Reset Mode (see below).
 * * m   SGR       Set attributes (see below).
 * * n   DSR       Status report (see below).
 * * q   DECLL     Set keyboard LEDs.
 *     * ESC [ 0 q: clear all LEDs
 *     * ESC [ 1 q: set Scroll Lock LED
 *     * ESC [ 2 q: set Num Lock LED
 *     * ESC [ 3 q: set Caps Lock LED
 * * r   DECSTBM   Set scrolling region; parameters are top and bottom row.
 * * s   ?         Save cursor location.
 * * u   ?         Restore cursor location.
 * * `   HPA       Move cursor to indicated column in current row.
 */
object CsiChars {

    @JvmStatic
    val eraseDisplayFromStartToCursor: String
        @JvmName("eraseDisplayFromStartToCursor") get() = eraseDisplay(1)

    @JvmStatic
    val eraseWholeDisplay: String
        @JvmName("eraseWholeDisplay") get() = eraseDisplay(2)

    @JvmStatic
    val eraseWholeDisplayIncludingBuffer: String
        @JvmName("eraseWholeDisplayIncludingBuffer") get() = eraseDisplay(3)

    @JvmStatic
    val eraseLineFromStartToCursor: String
        @JvmName("eraseLineFromStartToCursor") get() = eraseLine(1)

    @JvmStatic
    val eraseWholeLine: String
        @JvmName("eraseWholeLine") get() = eraseLine(2)

    @JvmStatic
    val displayControlChars: String
        @JvmName("displayControlChars") get() = setMode(3)

    @JvmStatic
    val setInsertMode: String
        @JvmName("setInsertMode") get() = setMode(4)

    @JvmStatic
    val followCr: String
        @JvmName("followCr") get() = setMode(20)

    @JvmStatic
    val resetDisplayControlChars: String
        @JvmName("resetDisplayControlChars") get() = resetMode(3)

    @JvmStatic
    val resetInsertMode: String
        @JvmName("resetInsertMode") get() = resetMode(4)

    @JvmStatic
    val resetFollowCr: String
        @JvmName("resetFollowCr") get() = resetMode(20)

    @JvmStatic
    val saveCursor: String
        @JvmName("saveCursor") get() = EscapeChars.csiChars("s")

    @JvmStatic
    val restoreCursor: String
        @JvmName("restoreCursor") get() = EscapeChars.csiChars("u")

    @JvmStatic
    val reportStatus: String
        @JvmName("reportStatus") get() = EscapeChars.csiChars("5n")

    @JvmStatic
    val reportCursor: String
        @JvmName("reportCursor") get() = EscapeChars.csiChars("6n")

    @JvmStatic
    @JvmOverloads
    fun cursorUp(n: Int = 1): String {
        return EscapeChars.csiChars("${n}A")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorDown(n: Int = 1): String {
        return EscapeChars.csiChars("${n}B")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorForward(n: Int = 1): String {
        return EscapeChars.csiChars("${n}C")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorBack(n: Int = 1): String {
        return EscapeChars.csiChars("${n}D")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorNextLine(n: Int = 1): String {
        return EscapeChars.csiChars("${n}E")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorPreviousLine(n: Int = 1): String {
        return EscapeChars.csiChars("${n}F")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorColumn(n: Int = 1): String {
        return EscapeChars.csiChars("${n}G")
    }

    @JvmStatic
    fun cursorMove(n: Int, m: Int): String {
        return EscapeChars.csiChars("${n};${m}H")
    }

    @JvmStatic
    @JvmOverloads
    fun scrollUp(n: Int = 1): String {
        return EscapeChars.csiChars("${n}S")
    }

    @JvmStatic
    @JvmOverloads
    fun scrollDown(n: Int = 1): String {
        return EscapeChars.csiChars("${n}T")
    }

    /**
     * @param n
     * * 1: erase from start to cursor
     * * 2: erase whole display
     * * 3: erase whole display including scroll-back buffer (since Linux 3.0)
     */
    @JvmStatic
    @JvmOverloads
    fun eraseDisplay(n: Int = 2): String {
        return EscapeChars.csiChars("${n}J")
    }

    /**
     * @param n
     * * 1: erase from start of line to cursor
     * * 2: erase whole line
     */
    @JvmStatic
    @JvmOverloads
    fun eraseLine(n: Int = 2): String {
        return EscapeChars.csiChars("${n}K")
    }

    /**
     * @param h
     * * 3: DECCRM (default off): Display control chars.
     * * 4: DECIM (default off): Set insert mode.
     * * 20: LF/NL (default off): Automatically follow echo of LF, VT or FF with CR.
     */
    @JvmStatic
    fun setMode(h: Int): String {
        return EscapeChars.csiChars("${h}h")
    }

    /**
     * @param l
     * * 3: DECCRM (default off): Display control chars.
     * * 4: DECIM (default off): Set insert mode.
     * * 20: LF/NL (default off): Automatically follow echo of LF, VT or FF with CR.
     */
    @JvmStatic
    fun resetMode(l: Int): String {
        return EscapeChars.csiChars("${l}l")
    }
}

/**
 * ECMA-48 Set Graphics Rendition:
 *
 * The  ECMA-48  SGR sequence ESC [ parameters m sets display attributes.  Several attributes
 * can be set in the same sequence, separated by semicolons.   An  empty  parameter  (between
 * semicolons or string initiator or terminator) is interpreted as a zero.
 */
object SgrChars {

    @JvmStatic
    val reset: String
        @JvmName("reset") get() = EscapeChars.csiChars("${SgrParam.RESET.value}m")

    @JvmStatic
    fun foregroundBlack(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_BLACK)
    }

    @JvmStatic
    fun foregroundRed(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_RED)
    }

    @JvmStatic
    fun foregroundGreen(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_GREEN)
    }

    @JvmStatic
    fun foregroundBrown(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_BROWN)
    }

    @JvmStatic
    fun foregroundBlue(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_BLUE)
    }

    @JvmStatic
    fun foregroundMagenta(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_MAGENTA)
    }

    @JvmStatic
    fun foregroundCyan(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_CYAN)
    }

    @JvmStatic
    fun foregroundWhite(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_WHITE)
    }

    @JvmStatic
    fun foregroundDefault(content: Any?): String {
        return withParam(content, SgrParam.FOREGROUND_DEFAULT)
    }

    @JvmStatic
    fun backgroundBlack(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_BLACK)
    }

    @JvmStatic
    fun backgroundRed(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_RED)
    }

    @JvmStatic
    fun backgroundGreen(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_GREEN)
    }

    @JvmStatic
    fun backgroundBrown(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_BROWN)
    }

    @JvmStatic
    fun backgroundBlue(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_BLUE)
    }

    @JvmStatic
    fun backgroundMagenta(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_MAGENTA)
    }

    @JvmStatic
    fun backgroundCyan(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_CYAN)
    }

    @JvmStatic
    fun backgroundWhite(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_WHITE)
    }

    @JvmStatic
    fun backgroundDefault(content: Any?): String {
        return withParam(content, SgrParam.BACKGROUND_DEFAULT)
    }

    @JvmStatic
    fun withParam(content: Any?, sgrParams: SgrParam): String {
        return EscapeChars.csiChars("${sgrParams.value}m${content}") + reset
    }

    @JvmStatic
    fun withParams(content: Any?, vararg sgrParams: SgrParam): String {
        return withParam(content, SgrParam.concat(*sgrParams))
    }

    @JvmStatic
    fun withParams(content: Any?, sgrParams: List<SgrParam>): String {
        return withParam(content, SgrParam.concat(sgrParams))
    }
}

/**
 * Parameters for [SgrChars].
 */
interface SgrParam {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val value: String
        @JvmName("value") get

    companion object {

        @JvmField
        val RESET: SgrParam = SgrParam.of("0")

        @JvmField
        val BOLD: SgrParam = SgrParam.of("1")

        @JvmField
        val HALF_BRIGHT: SgrParam = SgrParam.of("2")

        @JvmField
        val ITALIC: SgrParam = SgrParam.of("3")

        @JvmField
        val UNDERSCORE: SgrParam = SgrParam.of("4")

        @JvmField
        val BLINK: SgrParam = SgrParam.of("5")

        @JvmField
        val FAST_BLINK: SgrParam = SgrParam.of("6")

        @JvmField
        val INVERSE: SgrParam = SgrParam.of("7")

        @JvmField
        val INVISIBLE: SgrParam = SgrParam.of("8")

        @JvmField
        val STRIKETHROUGH: SgrParam = SgrParam.of("9")

        @JvmField
        val PRIMARY_FONT: SgrParam = SgrParam.of("10")

        @JvmField
        val ALTERNATE_FONT_1: SgrParam = SgrParam.of("11")

        @JvmField
        val ALTERNATE_FONT_2: SgrParam = SgrParam.of("12")

        @JvmField
        val ALTERNATE_FONT_3: SgrParam = SgrParam.of("13")

        @JvmField
        val ALTERNATE_FONT_4: SgrParam = SgrParam.of("14")

        @JvmField
        val ALTERNATE_FONT_5: SgrParam = SgrParam.of("15")

        @JvmField
        val ALTERNATE_FONT_6: SgrParam = SgrParam.of("16")

        @JvmField
        val ALTERNATE_FONT_7: SgrParam = SgrParam.of("17")

        @JvmField
        val ALTERNATE_FONT_8: SgrParam = SgrParam.of("18")

        @JvmField
        val ALTERNATE_FONT_9: SgrParam = SgrParam.of("19")

        @JvmField
        val BOLD_OFF: SgrParam = SgrParam.of("21")

        @JvmField
        val HALF_BRIGHT_OFF: SgrParam = SgrParam.of("22")

        @JvmField
        val ITALIC_OFF: SgrParam = SgrParam.of("23")

        @JvmField
        val UNDERSCORE_OFF: SgrParam = SgrParam.of("24")

        @JvmField
        val BLINK_OFF: SgrParam = SgrParam.of("25")

        @JvmField
        val FAST_BLINK_OFF: SgrParam = SgrParam.of("26")

        @JvmField
        val INVERSE_OFF: SgrParam = SgrParam.of("27")

        @JvmField
        val INVISIBLE_OFF: SgrParam = SgrParam.of("28")

        @JvmField
        val STRIKETHROUGH_OFF: SgrParam = SgrParam.of("29")

        @JvmField
        val FOREGROUND_BLACK: SgrParam = SgrParam.of("30")

        @JvmField
        val FOREGROUND_RED: SgrParam = SgrParam.of("31")

        @JvmField
        val FOREGROUND_GREEN: SgrParam = SgrParam.of("32")

        @JvmField
        val FOREGROUND_BROWN: SgrParam = SgrParam.of("33")

        @JvmField
        val FOREGROUND_BLUE: SgrParam = SgrParam.of("34")

        @JvmField
        val FOREGROUND_MAGENTA: SgrParam = SgrParam.of("35")

        @JvmField
        val FOREGROUND_CYAN: SgrParam = SgrParam.of("36")

        @JvmField
        val FOREGROUND_WHITE: SgrParam = SgrParam.of("37")

        @JvmField
        val FOREGROUND_DEFAULT: SgrParam = SgrParam.of("39")

        @JvmField
        val BACKGROUND_BLACK: SgrParam = SgrParam.of("40")

        @JvmField
        val BACKGROUND_RED: SgrParam = SgrParam.of("41")

        @JvmField
        val BACKGROUND_GREEN: SgrParam = SgrParam.of("42")

        @JvmField
        val BACKGROUND_BROWN: SgrParam = SgrParam.of("43")

        @JvmField
        val BACKGROUND_BLUE: SgrParam = SgrParam.of("44")

        @JvmField
        val BACKGROUND_MAGENTA: SgrParam = SgrParam.of("45")

        @JvmField
        val BACKGROUND_CYAN: SgrParam = SgrParam.of("46")

        @JvmField
        val BACKGROUND_WHITE: SgrParam = SgrParam.of("47")

        @JvmField
        val BACKGROUND_DEFAULT: SgrParam = SgrParam.of("49")

        @JvmField
        val FRAMED: SgrParam = SgrParam.of("51")

        @JvmField
        val ENCIRCLED: SgrParam = SgrParam.of("52")

        @JvmField
        val OVERLINE: SgrParam = SgrParam.of("53")

        @JvmField
        val FRAMED_ENCIRCLED_OFF: SgrParam = SgrParam.of("54")

        @JvmField
        val OVERLINE_OFF: SgrParam = SgrParam.of("55")

        @JvmField
        val FOREGROUND_BRIGHT_BLACK: SgrParam = SgrParam.of("90")

        @JvmField
        val FOREGROUND_BRIGHT_RED: SgrParam = SgrParam.of("91")

        @JvmField
        val FOREGROUND_BRIGHT_GREEN: SgrParam = SgrParam.of("92")

        @JvmField
        val FOREGROUND_BRIGHT_BROWN: SgrParam = SgrParam.of("93")

        @JvmField
        val FOREGROUND_BRIGHT_BLUE: SgrParam = SgrParam.of("94")

        @JvmField
        val FOREGROUND_BRIGHT_MAGENTA: SgrParam = SgrParam.of("95")

        @JvmField
        val FOREGROUND_BRIGHT_CYAN: SgrParam = SgrParam.of("96")

        @JvmField
        val FOREGROUND_BRIGHT_WHITE: SgrParam = SgrParam.of("97")

        @JvmField
        val BACKGROUND_BRIGHT_BLACK: SgrParam = SgrParam.of("100")

        @JvmField
        val BACKGROUND_BRIGHT_RED: SgrParam = SgrParam.of("101")

        @JvmField
        val BACKGROUND_BRIGHT_GREEN: SgrParam = SgrParam.of("102")

        @JvmField
        val BACKGROUND_BRIGHT_BROWN: SgrParam = SgrParam.of("103")

        @JvmField
        val BACKGROUND_BRIGHT_BLUE: SgrParam = SgrParam.of("104")

        @JvmField
        val BACKGROUND_BRIGHT_MAGENTA: SgrParam = SgrParam.of("105")

        @JvmField
        val BACKGROUND_BRIGHT_CYAN: SgrParam = SgrParam.of("106")

        @JvmField
        val BACKGROUND_BRIGHT_WHITE: SgrParam = SgrParam.of("107")

        @JvmStatic
        fun of(value: CharSequence): SgrParam {
            return SgrParamImpl(value.toString())
        }

        @JvmStatic
        fun concat(vararg params: SgrParam): SgrParam {
            return concat(params.toList())
        }

        @JvmStatic
        fun concat(params: List<SgrParam>): SgrParam {
            return SgrParamImpl(params.joinToString(separator = ";") { r -> r.value })
        }

        @JvmStatic
        fun alternateFont(n: Int): SgrParam {
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
        fun foregroundColor(n: Int): SgrParam {
            return SgrParamImpl("38;5;$n")
        }

        @JvmStatic
        fun foregroundColor(r: Int, g: Int, b: Int): SgrParam {
            return SgrParamImpl("38;2;$r;$g;$b")
        }

        @JvmStatic
        fun backgroundColor(n: Int): SgrParam {
            return SgrParamImpl("48;5;$n")
        }

        @JvmStatic
        fun backgroundColor(r: Int, g: Int, b: Int): SgrParam {
            return SgrParamImpl("48;2;$r;$g;$b")
        }

        private class SgrParamImpl(override val value: String) : SgrParam
    }
}