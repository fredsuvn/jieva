package xyz.srclab.common.egg.boat

interface OWeaponManager {

    fun fire(weapon: OWeapon, tick: Long): OBullet
}