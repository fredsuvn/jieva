package xyz.srclab.test.perform;

import com.google.common.collect.Iterables;

import java.io.PrintStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sunqian
 */
public class PerformResultsPrinter {

    private final List<PerformResult> performResults = new LinkedList<>();

    public PerformResultsPrinter addPerformResult(PerformResult performResult) {
        this.performResults.add(performResult);
        return this;
    }

    public PerformResultsPrinter addPerformResults(PerformResult... performResults) {
        return addPerformResults(Arrays.asList(performResults));
    }

    public PerformResultsPrinter addPerformResults(Iterable<PerformResult> performResults) {
        Iterables.addAll(this.performResults, performResults);
        return this;
    }

    public void print() {
        print(System.out, ChronoUnit.NANOS);
    }

    public void print(PrintStream printStream, TemporalUnit temporalUnit) {
        int titleLength = 0;
        int costTimeLength = 0;
        for (PerformResult performResult : performResults) {
            if (performResult.getTitle().length() > titleLength) {
                titleLength = performResult.getTitle().length();
            }
            String costTime = String.valueOf(parseCostTime(performResult.getCostTime(), temporalUnit));
            if (costTime.length() > costTimeLength) {
                costTimeLength = costTime.length();
            }
        }
        for (PerformResult performResult : performResults) {
            String message = String.format(
                    "%-" + titleLength + "s: %" + costTimeLength + "d " + temporalUnit.toString(),
                    performResult.getTitle(),
                    parseCostTime(performResult.getCostTime(), temporalUnit)
            );
            printStream.println(message);
        }
    }

    private long parseCostTime(Duration duration, TemporalUnit temporalUnit) {
        if (ChronoUnit.SECONDS.equals(temporalUnit) || ChronoUnit.NANOS.equals(temporalUnit)) {
            return duration.get(temporalUnit);
        }
        if (ChronoUnit.MILLIS.equals(temporalUnit)) {
            return duration.toMillis();
        }
        return duration.get(temporalUnit);
    }
}
