package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.plugin.animation.prop.StringDeobfuscationAnimationProp
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.ActionsArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import net.kyori.adventure.key.Key
import org.bukkit.entity.Player

@RegisterAction("string-deobfuscation")
class StringDeobfuscationAction : Action<PlayerScenario> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("deobfuscate-every", 1, false),
        PrimitiveObjectArgument("id", "example", false),
        PrimitiveObjectArgument("deobfuscation-string", "example", true),
        PrimitiveObjectArgument("obfuscated-format", "<obfuscated><gray>", true),
        PrimitiveObjectArgument("deobfuscated-format", "<white>", true),
        ActionsArgument("deobfuscation-actions", listOf(), false, PlayerScenario::class.java, listOf(
            ClassTransform(
                Player::class.java
            ) { a -> a.player })),
        PrimitiveObjectArgument("strip-colors", defaultValue = true, required = false),
    )

    override fun execute(binder: PlayerScenario, args: ObjectArguments, textUpdater: (PlayerScenario, String) -> String) {
        val deobfuscateEvery = args.int("deobfuscate-every") { textUpdater(binder, it) } ?: return
        val deobfuscationString = args.string("deobfuscation-string") { textUpdater(binder, it) } ?: return
        val obfuscatedFormat = args.string("obfuscated-format") { textUpdater(binder, it) } ?: return
        val deobfuscatedFormat = args.string("deobfuscated-format") { textUpdater(binder, it) } ?: return
        val deobfuscationActions = args.typed<Collection<ConfiguredExecutableObject<PlayerScenario, Unit>>>("deobfuscation-actions") { textUpdater(binder, it) } ?: return
        val id = args.string("id") { textUpdater(binder, it) } ?: "example"
        val stripColors = args.boolean("strip-colors") { textUpdater(binder, it) } ?: false

        val prop = StringDeobfuscationAnimationProp(
            id,
            binder,
            deobfuscateEvery,
            deobfuscationString,
            obfuscatedFormat,
            deobfuscatedFormat,
            deobfuscationActions,
            stripColors,
        )
        binder.props[Key.key("deobfuscation:$id")] = prop
    }
}