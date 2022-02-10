package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

open class OConfig : Serializable {

    var screenWidth: Int = 800
    var screenHeight: Int = 1200
    var preparedWidth: Int = 100
    var preparedHeight: Int = 100

    var unitX: Double = 5.0
    var unitY: Double = 5.0

    var weaponManager: String = OWeaponManager::class.java.name

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}