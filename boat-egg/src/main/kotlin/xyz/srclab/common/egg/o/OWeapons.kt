//package xyz.srclab.common.egg.o
//
//const val PLAYER_WEAPON = 100
//const val COMMON_WEAPON = 200
//const val FLOWER_WEAPON = 300
//const val QUICK_WEAPON = 400
//
//interface OWeapon {
//    fun fire(soldier: Soldier, weapon: Weapon, target: Soldier, now: Long)
//}
//
//fun Weapon.fire(soldier: Soldier, weapon: Weapon, target: Soldier, now: Long) {
//    val action = when (this.type) {
//        PLAYER_WEAPON -> PlayerWeapon
//        FLOWER_WEAPON -> PlayerWeapon
//        QUICK_WEAPON -> PlayerWeapon
//        else -> CommonWeapon
//    }
//    action.fire(soldier, weapon, target, now)
//}
//
//object PlayerWeapon : OWeapon {
//    override fun fire(soldier: Soldier, weapon: Weapon, target: Soldier, now: Long) {
//        val bullet = weapon.newBullet(soldier, target)
//        bullet.speed = 90
//        weapon.lastFireTime = now
//        weapon.lastSubFireTime = now
//    }
//}
//
//object CommonWeapon : OWeapon {
//    override fun fire(soldier: Soldier, weapon: Weapon, target: Soldier, now: Long) {
//        val bullet = weapon.newBullet(soldier, target)
//        bullet.speed = 80
//        weapon.lastFireTime = now
//        weapon.lastSubFireTime = now
//    }
//}
//
//object FlowerWeapon : OWeapon {
//    override fun fire(soldier: Soldier, weapon: Weapon, target: Soldier, now: Long) {
//        fun newBullet(soldier: Soldier, stepX: Double, stepY: Double) {
//            val bullet = Bullet()
//            bullet.x = soldier.x
//            bullet.y = soldier.y
//            bullet.stepX = stepX
//            bullet.stepY = stepY
//            bullet.damage = weapon.damage
//            bullet.speed = 80
//            weapon.bullets!!.add(bullet)
//        }
//        newBullet(soldier, -1.0, 0.0)
//        newBullet(soldier, -SLASH_STEP, -SLASH_STEP)
//        newBullet(soldier, 0.0, -1.0)
//        newBullet(soldier, SLASH_STEP, -SLASH_STEP)
//        newBullet(soldier, 1.0, 0.0)
//        newBullet(soldier, SLASH_STEP, SLASH_STEP)
//        newBullet(soldier, 0.0, 1.0)
//        newBullet(soldier, -SLASH_STEP, SLASH_STEP)
//        weapon.lastFireTime = now
//        weapon.lastSubFireTime = now
//    }
//}
//
//object QuickWeapon : OWeapon {
//    private const val crevasseTime = 7
//    override fun fire(soldier: Soldier, weapon: Weapon, target: Soldier, now: Long) {
//        val bullet = weapon.newBullet(soldier, target)
//        bullet.speed = 90
//        weapon.lastFireTime = now
//        if (now - )
//        weapon.lastSubFireTime = now
//    }
//}
//
//private fun Weapon.newBullet(soldier: Soldier, target: Soldier): Bullet {
//    val bullet = Bullet()
//    bullet.x = soldier.x
//    bullet.y = soldier.y
//    val step = step(bullet.x, bullet.y, target.x, target.y)
//    bullet.stepX = step.x
//    bullet.stepY = step.y
//    bullet.damage = this.damage
//    this.bullets!!.add(bullet)
//    return bullet
//}