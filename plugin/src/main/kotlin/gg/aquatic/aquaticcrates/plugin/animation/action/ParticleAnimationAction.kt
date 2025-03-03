package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.color.AlphaColor
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.particle.Particle
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.particle.data.*
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.particle.type.ParticleType
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.particle.type.ParticleTypes
import gg.aquatic.waves.shadow.com.retrooper.packetevents.util.Vector3d
import gg.aquatic.waves.shadow.com.retrooper.packetevents.util.Vector3f
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle
import gg.aquatic.waves.shadow.io.retrooper.packetevents.util.SpigotConversionUtil
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.ItemObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.item.toCustomItem
import gg.aquatic.waves.util.toUser
import org.bukkit.Bukkit
import org.bukkit.Material

class ParticleAnimationAction : Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("particle", "example", true),
        PrimitiveObjectArgument("block-material", "stone", false),
        PrimitiveObjectArgument("dust-scale", 1f, false),
        PrimitiveObjectArgument("color", "0;0;0", false),
        PrimitiveObjectArgument("start-color", "0;0;0", false),
        PrimitiveObjectArgument("end-color", "0;0;0", false),
        PrimitiveObjectArgument("sculk-roll", 0f, false),
        PrimitiveObjectArgument("delay", 1, false),
        PrimitiveObjectArgument("vector", "0;0;0", false),
        PrimitiveObjectArgument("duration", 1, false),
        PrimitiveObjectArgument("location-offset", null, false),
        PrimitiveObjectArgument("offset", "0;0;0", false),
        PrimitiveObjectArgument("speed", 0f, false),
        PrimitiveObjectArgument("count", 1, false),
        ItemObjectArgument("item", Material.STONE.toCustomItem(), false)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val particleId = args.string("particle") { textUpdater(binder, it) } ?: return

        val particleType = ParticleTypes.getByName(particleId.lowercase())
        val particle = when (particleType) {
            ParticleTypes.BLOCK, ParticleTypes.BLOCK_MARKER, ParticleTypes.FALLING_DUST, ParticleTypes.DUST_PLUME, ParticleTypes.BLOCK_CRUMBLE -> {
                val blockMaterial =
                    Material.valueOf((args.string("block-material") { textUpdater(binder, it) } ?: "stone").uppercase())
                Particle(
                    (particleType as ParticleType<ParticleBlockStateData>),
                    ParticleBlockStateData(SpigotConversionUtil.fromBukkitBlockData(blockMaterial.createBlockData()))
                )
            }

            ParticleTypes.DUST -> {
                val scale = args.float("dust-scale") { textUpdater(binder, it) } ?: 1f
                val color = args.string("color") { textUpdater(binder, it) } ?: "0;0;0"
                val rgb = color.split(";").map { it.toIntOrNull() ?: 0 }
                Particle(
                    (particleType as ParticleType<ParticleDustData>),
                    ParticleDustData(scale, rgb[0], rgb[1], rgb[2])
                )
            }

            ParticleTypes.DUST_COLOR_TRANSITION -> {
                val scale = args.float("dust-scale") { textUpdater(binder, it) } ?: 1f
                val startColor = args.string("start-color") { textUpdater(binder, it) } ?: "0;0;0"
                val endColor = args.string("end-color") { textUpdater(binder, it) } ?: "0;0;0"
                val rgb1 = startColor.split(";").map { it.toFloatOrNull() ?: 0f }
                val rgb2 = endColor.split(";").map { it.toFloatOrNull() ?: 0f }

                Particle(
                    (particleType as ParticleType<ParticleDustColorTransitionData>),
                    ParticleDustColorTransitionData(scale, rgb1[0], rgb1[1], rgb1[2], rgb2[0], rgb2[1], rgb2[2])
                )
            }

            ParticleTypes.ENTITY_EFFECT -> {
                val color = args.string("color") { textUpdater(binder, it) } ?: "0;0;0;0"
                val rgb = color.split(";").map { it.toIntOrNull() ?: 0 }

                Particle(
                    particleType as ParticleType<ParticleColorData>,
                    ParticleColorData(AlphaColor(rgb.getOrNull(3) ?: 1, rgb[0], rgb[1], rgb[2]))
                )
            }

            ParticleTypes.SCULK_CHARGE -> {
                val sculkRoll = args.float("sculk-roll") { textUpdater(binder, it) } ?: 0f
                Particle(
                    particleType as ParticleType<ParticleSculkChargeData>,
                    ParticleSculkChargeData(sculkRoll)
                )
            }

            ParticleTypes.ITEM -> {
                val item = args.typed<AquaticItem>("item") { textUpdater(binder, it) } ?: Material.STONE.toCustomItem()
                Particle(
                    (particleType as ParticleType<ParticleItemStackData>),
                    ParticleItemStackData(SpigotConversionUtil.fromBukkitItemStack(item.getItem()))
                )
            }

            ParticleTypes.SHRIEK -> {
                val delay = args.int("delay") { textUpdater(binder, it) } ?: 1
                Particle(
                    (particleType as ParticleType<ParticleShriekData>),
                    ParticleShriekData(delay)
                )
            }

            ParticleTypes.TRAIL -> {
                val duration = args.int("duration") { textUpdater(binder, it) } ?: 1
                val vector = args.string("vector") { textUpdater(binder, it) } ?: "0;0;0"
                val actualVector = vector.split(";").map { it.toDoubleOrNull() ?: 0.0 }
                val rgb = vector.split(";").map { it.toFloatOrNull() ?: 0f }
                val color =
                    gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.color.Color(rgb[0], rgb[1], rgb[2])
                Particle(
                    (particleType as ParticleType<ParticleTrailData>),
                    ParticleTrailData(
                        Vector3d(actualVector[0], actualVector[1], actualVector[2]),
                        color,
                        duration
                    )
                )
            }

            else -> {
                Particle(
                    particleType
                )
            }
        }

        val locationOffsets = args.stringOrCollection("location-offset") { textUpdater(binder, it) } ?: listOf("0;0;0")

        val packets = mutableListOf<WrapperPlayServerParticle>()
        for (locationOffsetStr in locationOffsets) {
            val offsetStr = args.string("offset") { textUpdater(binder, it) } ?: "0;0;0"
            val locationOffset = locationOffsetStr.split(";").map { it.toDoubleOrNull() ?: 0.0 }
            val offset = offsetStr.split(";").map { it.toFloatOrNull() ?: 0f }

            val speed = args.float("speed") { textUpdater(binder, it) } ?: 1f
            val count = args.int("count") { textUpdater(binder, it) } ?: 1
            val baseVector = Vector3d(
                binder.baseLocation.x + locationOffset[0],
                binder.baseLocation.y + locationOffset[1],
                binder.baseLocation.z + locationOffset[2]
            )

            val packet = WrapperPlayServerParticle(
                particle,
                true,
                baseVector,
                Vector3f(offset[0], offset[1], offset[2]),
                speed,
                count
            )
            packets += packet

        }

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.location.world != binder.baseLocation.world) continue
            if (onlinePlayer.location.distanceSquared(binder.baseLocation) > 1000) continue

            if (!binder.audience.canBeApplied(onlinePlayer)) continue

            onlinePlayer.toUser()?.let {
                for (packet in packets) {
                    it.sendPacket(packet)
                }
            }
        }

    }
}