package xyz.srclab.common.egg.sample

import xyz.srclab.common.egg.Egg

/**
 * Controller of [Egg].
 */
interface Controller<D : Data<*>> {

    val data: D

    fun start()

    fun stop()

    fun go()

    fun pause()

    fun toggle()
}