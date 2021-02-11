package xyz.srclab.common.egg

/**
 * Help design the view of [Egg].
 */
interface View<E : Engine<S, P>, S : Scenario, P : Playing> {

    fun start(fps: Int, engine: E, scenario: S)
}