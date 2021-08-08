package xyz.srclab.common.egg.o

import java.util.*

class OData {

    val players: MutableList<Player>
    val computers: MutableList<Soldier>
    val tick: OTick
    val config: OConfig

    constructor() {
        players = LinkedList()
        computers = LinkedList()
        tick = OTick()
        config = OConfig()
    }

    constructor(save: OSave) {
        players = LinkedList(save.players ?: emptyList())
        computers = LinkedList(save.computers ?: emptyList())
        tick = OTick(save.tick)
        config = save.config ?: OConfig()
    }
}