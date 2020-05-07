@file:JvmName("Performer")

package xyz.srclab.test.perform

import java.io.PrintStream
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

fun doPerform(info: PerformInfo, times: Long): PerformResult {
    val startTime = System.currentTimeMillis()
    val action = info.action
    for (i in 1..times) {
        action.run()
    }
    val endTime = System.currentTimeMillis()
    return PerformResult(info.title, Duration.ofMillis(endTime - startTime))
}

fun doPerforms(
    performInfos: Iterable<PerformInfo>,
    times: Long,
    printStream: PrintStream = System.out,
    temporalUnit: TemporalUnit = ChronoUnit.MILLIS
) {
    val printer = PerformResultsPrinter()
    for (performInfo in performInfos) {
        val result = doPerform(performInfo, times)
        printStream.println("${result.title} was completed.")
        printer.addPerformResult(result)
    }
    printStream.println("All complete>>>>>>>>>>>>>>>>>>")
    printer.print(printStream, temporalUnit)
}