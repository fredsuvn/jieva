package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.loadPropertiesResource
import xyz.srclab.common.collect.map



internal object OTypeManager {

    private val subjectTypes: Map<Int, OSubjectType> by lazy {
        "o/subjects.properties".loadPropertiesResource().map { k, v ->
            k.toInt() to run {
                val args = v.split(",")
                OSubjectType(
                    args[0].toDouble(),
                    args[1].toInt(),
                    args[2].toLong(),
                    args[3].toBoolean(),
                    args[4].toInt(),
                )
            }
        }
    }

    private val weaponTypes: Map<Int, OWeaponType> by lazy {
        "o/weapons.properties".loadPropertiesResource().map { k, v ->
            k.toInt() to run {
                val args = v.split(",")
                OWeaponType(
                    args[0].toInt(),
                    args[1].toInt(),
                    args[2].toDouble(),
                    args[3].toInt(),
                    args[4].toLong(),
                    args[4].toInt(),
                    args[4].toInt(),
                )
            }
        }
    }

    fun getSubjectType(typeId: Int): OSubjectType {
        return subjectTypes[typeId]!!
    }

    fun getWeaponType(typeId: Int): OWeaponType {
        return weaponTypes[typeId]!!
    }
}