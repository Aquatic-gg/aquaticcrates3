package gg.aquatic.aquaticcrates.plugin.animation.prop

import com.destroystokyo.paper.profile.PlayerProfile
import gg.aquatic.waves.Waves
import gg.aquatic.waves.api.nms.PacketEntity
import gg.aquatic.waves.api.nms.profile.GameEventAction
import gg.aquatic.waves.api.nms.profile.ProfileEntry
import gg.aquatic.waves.api.nms.profile.UserProfile
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.PlayerScenarioProp
import gg.aquatic.waves.scenario.prop.Moveable
import gg.aquatic.waves.scenario.prop.path.PathBoundProperties
import gg.aquatic.waves.scenario.prop.path.PathProp
import gg.aquatic.waves.util.modify
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.runSync
import gg.aquatic.waves.util.sendPacket
import gg.aquatic.waves.util.version.ServerVersion
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CameraAnimationProp(
    override val scenario: PlayerScenario,
    val location: Location,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, Pair<PathBoundProperties, Int>>,
    override val locationOffsetYawPitch: Pair<Float, Float>,
) : PlayerScenarioProp, Moveable {

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()
    override fun move(location: Location) {
        smoothTeleport(location)
    }

    val packetEntity: PacketEntity = createEntity()

    private fun createEntity(): PacketEntity {
        val pe = Waves.NMS_HANDLER.createEntity(location, EntityType.BLOCK_DISPLAY, null)
            ?: throw Exception("Failed to create entity")
        return pe
    }

    val previousGamemode = scenario.player.gameMode
    val previousLocation = scenario.player.location.clone()
    val wasFlying = scenario.player.allowFlight

    fun attachPlayer() {
        val delay = if (previousLocation.world == location.world) {
            0
        } else 5
        scenario.player.teleportAsync(location).thenAccept {
            runSync {
                scenario.player.isInvisible = true
                scenario.player.gameMode = GameMode.SPECTATOR
            }
            runLaterSync(delay.toLong()) {
                val spawnPacket = packetEntity.spawnPacket
                val player = scenario.player
                player.sendPacket(spawnPacket)

                val listOrder = if (ServerVersion.ofAquatic(Waves.INSTANCE)?.isOlder(ServerVersion.V_1_21_4) ?: true) {
                    0
                } else modernPlayerListOrder(player)

                val infopacket = Waves.NMS_HANDLER.createPlayerInfoUpdatePacket(2, ProfileEntry(
                    player.playerProfile.toUserProfile(),
                    true,
                    0,
                    GameMode.CREATIVE,
                    null,
                    true,
                    listOrder
                ))

                val gameEventPacket = Waves.NMS_HANDLER.createChangeGameStatePacket(GameEventAction.CHANGE_GAME_MODE,
                    GameMode.SPECTATOR.value.toFloat())

                val spectatorPacket = Waves.NMS_HANDLER.createCameraPacket(packetEntity.entityId)
                player.sendPacket(infopacket)
                player.sendPacket(gameEventPacket)
                player.sendPacket(spectatorPacket)
            }
        }
    }

    private fun modernPlayerListOrder(player: Player): Int {
        return player.playerListOrder
    }

    private fun PlayerProfile.toUserProfile(): UserProfile {
        val profile = UserProfile(
            this.id ?: UUID.randomUUID(),
            this.name ?: "",
            this.properties.map {
                UserProfile.TextureProperty(it.name, it.value, it.signature ?: "")
            }.toMutableList()
        )
        return profile
    }

    override fun tick() {
        if (wasFlying) return
        scenario.player.allowFlight = true
        scenario.player.isFlying = true
    }

    override fun onEnd() {

    }

    fun setTeleportInterpolation(interpolation: Int) {
        packetEntity.modify {
            val display = it as BlockDisplay
            display.teleportDuration = interpolation
        }
        scenario.player.sendPacket(packetEntity.updatePacket!!,false)
    }

    fun smoothTeleport(location: Location) {
        setTeleportInterpolation(2)
        packetEntity.teleport(Waves.NMS_HANDLER,location,false,scenario.player)
    }

    fun teleport(location: Location) {
        setTeleportInterpolation(0)
        packetEntity.teleport(Waves.NMS_HANDLER,location,false,scenario.player)
    }

    fun detach() {
        val player = scenario.player
        val gameEventPacket = Waves.NMS_HANDLER.createChangeGameStatePacket(GameEventAction.CHANGE_GAME_MODE,previousGamemode.value.toFloat())
        val cameraPacket = Waves.NMS_HANDLER.createCameraPacket(player.entityId)
        player.sendPacket(cameraPacket)
        player.sendPacket(gameEventPacket)
        if (wasFlying) return
        player.isFlying = false
        player.allowFlight = false
    }
}