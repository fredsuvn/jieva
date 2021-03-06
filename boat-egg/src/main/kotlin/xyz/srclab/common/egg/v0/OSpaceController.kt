package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.sample.Controller

internal interface OSpaceController : Controller<OSpaceData, OSpaceScenario> {

    val config: OSpaceConfig

    val tick: OSpaceTick

    val logger: OSpaceLogger

    fun moveLeft(player: Int)

    fun moveRight(player: Int)

    fun moveUp(player: Int)

    fun moveDown(player: Int)

    fun moveLeftUp(player: Int)

    fun moveRightUp(player: Int)

    fun moveLeftDown(player: Int)

    fun moveRightDown(player: Int)

    fun fire(player: Int)

    fun pressKey(vk: Int)

    fun releaseKey(vk: Int)
}