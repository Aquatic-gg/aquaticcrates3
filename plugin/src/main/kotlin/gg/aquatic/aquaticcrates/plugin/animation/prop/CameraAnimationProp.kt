package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.player.GameMode
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import gg.aquatic.waves.shadow.io.retrooper.packetevents.util.SpigotConversionUtil
import gg.aquatic.waves.shadow.io.retrooper.packetevents.util.SpigotReflectionUtil
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.toUser
import org.bukkit.Location
import java.util.*

class CameraAnimationProp(
    override val animation: Animation,
    val location: Location
) : AnimationProp() {

    val entityId = SpigotReflectionUtil.generateEntityId()

    val previousGamemode = animation.player.gameMode
    val previousLocation = animation.player.location.clone()

    fun attachPlayer() {
        val builder = EntityDataBuilder.ANY
        builder.isSilent(true)
        builder.isInvisible(true)

        val delay = if (previousLocation.world == location.world) {
            0
        } else 5
        animation.player.teleport(location)
        runLaterSync(delay.toLong()) {
            val data = builder.build()
            val spawnPacket = WrapperPlayServerSpawnEntity(
                entityId,
                UUID.randomUUID(),
                EntityTypes.ARMOR_STAND,
                SpigotConversionUtil.fromBukkitLocation(location),
                location.yaw,
                0,
                null
            )
            val dataPacket = WrapperPlayServerEntityMetadata(entityId, data)
            val user = animation.player.toUser()
            user.sendPacket(spawnPacket)
            user.sendPacket(dataPacket)

            val playerInfoPacket = WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.UPDATE_GAME_MODE,
                WrapperPlayServerPlayerInfo.PlayerData(
                    null,
                    null,
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

    override fun onAnimationEnd() {
        val spectatorPacket = WrapperPlayServerCamera(0)
        val gameModePacket = WrapperPlayServerChangeGameState(
            WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE,
            previousGamemode.ordinal.toFloat()
        )
        animation.player.toUser().let {
            it.sendPacket(spectatorPacket)
            it.sendPacket(gameModePacket)
        }
        animation.player.gameMode = previousGamemode
        animation.player.teleport(previousLocation)
    }
}