package xyz.srclab.common.egg.sample

import xyz.srclab.common.egg.Egg

/**
 * Engine of [Egg].
 */
interface Engine<C : Controller<D, S>, D : Data<S>, S : Scenario> {

    fun loadNew(): C

    fun load(data: D): C
}