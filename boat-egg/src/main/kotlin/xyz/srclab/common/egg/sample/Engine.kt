package xyz.srclab.common.egg.sample

import xyz.srclab.common.egg.Egg

/**
 * Engine of [Egg].
 */
interface Engine<C : Controller<D>, D : Data<*>> {

    fun loadNew(): C

    fun load(data: D): C
}