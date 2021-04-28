package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.loadPropertiesResource
import xyz.srclab.common.collect.map

internal object OTypeManager {

    private val subjectTypes: Map<String, OSubjectType> by lazy {
        "o/subjects.properties".loadPropertiesResource().map { k, v ->
            k.trim() to run {
                val args = v.split(",")
                OSubjectType(
                    args[0].toDouble(),
                    args[1].toInt(),
                    args[2].toLong(),
                    args[3].toBoolean(),
                    args[4].trim(),
                )
            }
        }
    }

    private val weaponTypes: Map<String, OWeaponType> by lazy {
        "o/weapons.properties".loadPropertiesResource().map { k, v ->
            k.trim() to run {
                val args = v.split(",")
                OWeaponType(
                    args[0].toInt(),
                    args[1].toInt(),
                    args[2].toDouble(),
                    args[3].toInt(),
                    args[4].toLong(),
                    args[5].trim(),
                    args[6].trim(),
                )
            }
        }
    }

    fun getSubjectType(typeId: String): OSubjectType {
        return subjectTypes[typeId]!!
    }

    fun getWeaponType(typeId: String): OWeaponType {
        return weaponTypes[typeId]!!
    }
}

internal data class OSubjectType(
    var radius: Double,
    var moveSpeed: Int,
    var deathDuration: Long,
    var keepBody: Boolean,
    var drawId: String,
)

internal data class OWeaponType(
    var damage: Int,
    var fireSpeed: Int,
    var ammoRadius: Double,
    var ammoMoveSpeed: Int,
    var ammoDeathDuration: Long,
    var ammoDrawId: String,
    var actorId: String,
)