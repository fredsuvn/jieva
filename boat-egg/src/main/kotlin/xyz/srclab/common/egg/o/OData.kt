package xyz.srclab.common.egg.o

class OData {

    val group1: Group
    val group2: Group
    val tick: OTick

    constructor() {
        group1 = Group()
        group2 = Group()
        tick = OTick()
    }

    constructor(save: OSave) {
        group1 = Group()
        group2 = Group()
        tick = OTick(save.tick)
        group1.players.putAll(save.group1?.players ?: emptyMap())
        group1.soldiers.putAll(save.group1?.soldiers ?: emptyMap())
        group2.players.putAll(save.group2?.players ?: emptyMap())
        group2.soldiers.putAll(save.group2?.soldiers ?: emptyMap())
    }

    open class Group {
        var players: MutableMap<Long, Player> = HashMap()
        var soldiers: MutableMap<Long, Soldier> = HashMap()
    }
}