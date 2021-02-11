package xyz.srclab.common.egg

/**
 * Help design the engine of [Egg].
 */
interface Engine<S : Scenario, P : Playing> {

    fun start(scenario: S): P
}