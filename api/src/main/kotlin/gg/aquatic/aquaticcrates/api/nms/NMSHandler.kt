package gg.aquatic.aquaticcrates.api.nms

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.Consumer

interface NMSHandler {

    fun spawnEntity(location: Location, factory: Consumer<Entity>, players: List<Player>, type: String)
    fun despawnEntity(ids: List<Int>, players: List<Player>)
    fun updateEntity(id: Int, factory: Consumer<Entity>)
    fun throwEntity(id: Int, vector: Vector)
    fun teleportEntity(id: Int, vector: Vector)
    fun moveEntity(id: Int, location: Location)
    fun getEntity(id: Int): Entity
    fun setCamera(id: Int, player: Player)
    fun changeGamemode(player: Player, gameMode: GameMode)
    fun setPlayerInfo(action: String, player: Player, gameMode: GameMode)

}