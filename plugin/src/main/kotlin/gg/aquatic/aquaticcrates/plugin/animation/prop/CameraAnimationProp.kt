package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.PlayerBoundAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.player.GameMode
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import gg.aquatic.waves.shadow.io.retrooper.packetevents.util.SpigotConversionUtil
import gg.aquatic.waves.shadow.io.retrooper.packetevents.util.SpigotReflectionUtil
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.runSync
import gg.aquatic.waves.util.toUser
import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CameraAnimationProp(
    override val animation: PlayerBoundAnimation,
    val location: Location,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, Pair<PathBoundProperties, Int>>,
) : PlayerBoundAnimationProp(), MovableAnimationProp {

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()
    override fun move(location: Location) {
        smoothTeleport(location)
    }

    val entityId = SpigotReflectionUtil.generateEntityId()

    val previousGamemode = animation.player.gameMode
    val previousLocation = animation.player.location.clone()

    fun attachPlayer() {

        val delay = if (previousLocation.world == location.world) {
            0
        } else 5
        animation.player.teleport(location.clone().add(Vector(0.0, 2.5, 0.0)))
        runLaterSync(delay.toLong()) {
            val spawnPacket = WrapperPlayServerSpawnEntity(
                entityId,
                UUID.randomUUID(),
                EntityTypes.BLOCK_DISPLAY,
                SpigotConversionUtil.fromBukkitLocation(location),
                location.yaw,
                0,
                null
            )
            val user = animation.player.toUser()
            user.sendPacket(spawnPacket)

            val playerInfoPacket = WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.UPDATE_GAME_MODE,
                WrapperPlayServerPlayerInfo.PlayerData(
                    null,
                    user.profile,
                    GameMode.CREATIVE,
                    0
                )
            )
            val gameModePacket = WrapperPlayServerChangeGameState(
                WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE,
                GameMode.SPECTATOR.id.toFloat()
            )
            val spectatorPacket = WrapperPlayServerCamera(entityId)
            user.sendPacket(playerInfoPacket)
            user.sendPacket(gameModePacket)
            user.sendPacket(spectatorPacket)
        }
    }

    override fun tick() {

    }

    fun setTeleportInterpolation(interpolation: Int) {
        val dataBuilder = EntityDataBuilder.BLOCK_DISPLAY().setPosRotInterpolationDuration(interpolation)
        val metadataPacket = WrapperPlayServerEntityMetadata(entityId, dataBuilder.build())
        animation.player.toUser().sendPacket(metadataPacket)
    }

    fun smoothTeleport(location: Location) {
        setTeleportInterpolation(2)
        val teleportPacket =
            WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false)
        animation.player.toUser().sendPacket(teleportPacket)
    }

    fun teleport(location: Location) {
        setTeleportInterpolation(0)
        val teleportPacket =
            WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false)
        animation.player.toUser().sendPacket(teleportPacket)
    }

    override fun onAnimationEnd() {
        runSync {
            try {
                val gameModePacket = WrapperPlayServerChangeGameState(
                    WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE,
                    SpigotConversionUtil.fromBukkitGameMode(previousGamemode).id.toFloat()
                )

                animation.player.toUser().let {
                    it.sendPacket(WrapperPlayServerCamera(animation.player.toUser().entityId))
                    it.sendPacket(gameModePacket)
                }
                animation.player.gameMode = previousGamemode
                animation.player.teleport(previousLocation)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }
}