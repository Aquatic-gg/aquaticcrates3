package gg.aquatic.aquaticcrates.plugin.misc.hook

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.comet.api.CometRegistry
import gg.aquatic.comet.api.emitter.AbstractEmitter
import gg.aquatic.comet.api.emitter.AbstractUnrealizedEmitter
import gg.aquatic.comet.api.emitter.parent.Pose
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.registerAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import org.bukkit.util.Vector
import org.joml.Quaterniond
import org.joml.Vector3d

class CometHook {

    init {
        WavesRegistry.registerAction("show-comet-particle", ShowCometParticleAction())
        WavesRegistry.registerAction("hide-comet-particle", HideCometParticleAction())
    }

    class ShowCometParticleAction : Action<Animation> {
        override val arguments: List<AquaticObjectArgument<*>> = listOf(
            PrimitiveObjectArgument("id", "example", true),
            PrimitiveObjectArgument("emitter", "example", true),
            PrimitiveObjectArgument("offset", "0;0;0", false)
        )

        override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
            val id = args.string("id") { textUpdater(binder, it) } ?: return
            val emitterId = args.string("emitter") { textUpdater(binder, it) } ?: return
            val offsetWithYawPitch = args.string("offset") { textUpdater(binder, it) } ?: "0;0;0"

            val offsetArgs = offsetWithYawPitch.split(";")
            val offset = Vector(
                offsetArgs.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
                offsetArgs.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                offsetArgs.getOrNull(2)?.toDoubleOrNull() ?: 0.0
            )
            val yaw = offsetArgs.getOrNull(3)?.toFloatOrNull() ?: 0f
            val pitch = offsetArgs.getOrNull(4)?.toFloatOrNull() ?: 0f

            val emitter = CometRegistry.unrealizedEmitterByID(emitterId) ?: return
            val prop = CometParticleAnimationProp(binder, emitter, offset, yaw, pitch)
            binder.props.remove(id)?.onAnimationEnd()
            binder.props[id] = prop
        }
    }

    class HideCometParticleAction : Action<Animation> {
        override val arguments: List<AquaticObjectArgument<*>> = listOf(
            PrimitiveObjectArgument("id", "example", true)
        )

        override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
            val id = args.string("id") { textUpdater(binder, it) } ?: return
            binder.props.remove(id)?.onAnimationEnd()
        }
    }

    class CometParticleAnimationProp(
        override val animation: Animation,
        emitter: AbstractUnrealizedEmitter,
        val offset: Vector,
        val yawOffset: Float,
        val pitchOffset: Float
    ) :
        AnimationProp() {



        private var spawnedEmitter: AbstractEmitter? = null
        private var killed = false

        init {
            val loc = animation.baseLocation.clone().apply {
                add(offset)
                yaw += yawOffset
                pitch += pitchOffset
            }

            emitter.realize(
                null, Pose(
                    loc.world,
                    Vector3d(loc.x, loc.y, loc.z),
                    Quaterniond().rotateX(pitchOffset.toDouble()).rotateY(yawOffset.toDouble())
                ), audience = animation.audience, after = { spawnedEmitter ->
                    this.spawnedEmitter = spawnedEmitter
                    if (killed) {
                        spawnedEmitter.kill()
                    }
                }
            )
        }

        override fun tick() {

        }

        override fun onAnimationEnd() {
            killed = true
            spawnedEmitter?.kill()
        }
    }
}