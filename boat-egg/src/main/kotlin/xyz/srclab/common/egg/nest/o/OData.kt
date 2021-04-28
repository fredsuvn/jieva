package xyz.srclab.common.egg.nest.o

import java.util.*

/**
 * @author sunqian
 */
internal data class OData(
    val player1: OPlayer,
    val player2: OPlayer,
    val playerSubjects: MutableList<OSubject> = LinkedList(),
    val enemySubjects: MutableList<OSubject> = LinkedList(),
    val playerAmmos: MutableList<OAmmo> = LinkedList(),
    val enemieAmmos: MutableList<OAmmo> = LinkedList(),
)