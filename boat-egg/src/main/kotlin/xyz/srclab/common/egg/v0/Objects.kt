package xyz.srclab.common.egg.v0

import java.awt.Graphics

interface Living {

    var hp: Int

    var speed: Int

    val weapons: List<Weapon>

    var deadTime: Long

    fun showLiving(graphics: Graphics, x: Int, y: Int)

    fun showDead(graphics: Graphics, x: Int, y: Int)
}

interface Weapon {

    var damage: Int

    var speed: Int

    var coolDownTime: Long
}

interface Ammo {

    fun show(graphics: Graphics, x: Int, y: Int)
}