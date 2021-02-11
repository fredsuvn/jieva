package xyz.srclab.common.egg

/**
 * Help design the engine of [Egg].
 */
interface Engine {

    fun start(scenario: Scenario): Playing
}