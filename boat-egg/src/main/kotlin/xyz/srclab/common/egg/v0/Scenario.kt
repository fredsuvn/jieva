package xyz.srclab.common.egg.v0

/**
 * @author sunqian
 */
interface Scenario {

    val refreshInterval: Long

    fun generateEnemies(difficulty: Int, weight: Int, preparedHeight: Int): List<Enemy>
}