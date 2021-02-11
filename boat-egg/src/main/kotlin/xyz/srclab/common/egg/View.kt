package xyz.srclab.common.egg

/**
 * Help design the view of [Egg].
 */
interface View {

    fun start(fps: Int, engine: Engine, scenario: Scenario)
}