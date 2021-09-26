package xyz.srclab.common.run

interface StatisticsFunction<V> : () -> V, RunningStatistics {
}