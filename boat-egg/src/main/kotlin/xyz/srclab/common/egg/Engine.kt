package xyz.srclab.common.egg

/**
 * Help design the engine of [Egg].
 */
interface Engine<S : Scenario, C : Controller<D>, D : Data> {

    fun load(scenario: S): C
}