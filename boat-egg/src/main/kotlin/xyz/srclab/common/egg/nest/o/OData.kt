package xyz.srclab.common.egg.nest.o

import java.util.*

/**
 * @author sunqian
 */
internal data class OData(
    val player1: OPlayer,
    val player2: OPlayer,
    val playerEnemy: OPlayer,
    val humanSubjects: MutableList<OSubject> = LinkedList(),
    val enemySubjects: MutableList<OSubject> = LinkedList(),
    val humanAmmos: MutableList<OAmmo> = LinkedList(),
    val enemyAmmos: MutableList<OAmmo> = LinkedList(),
)