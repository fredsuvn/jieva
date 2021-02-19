package xyz.srclab.common.egg.sample

import xyz.srclab.common.egg.Egg

/**
 * Controller of [Egg].
 */
interface Controller<D : Data<S>, S : Scenario> {

    fun startNew()

    fun go()

    fun pause()

    fun stop()

    fun save(): D

    fun load(data: D)
}