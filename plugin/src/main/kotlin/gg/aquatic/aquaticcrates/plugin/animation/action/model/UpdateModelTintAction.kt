package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key
import org.bukkit.Color

@gg.aquatic.waves.util.action.RegisterAction("update-model-tint")
class UpdateModelTintAction: Action<Scenario> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("tint", "255;255;255", true),
    )

    override fun execute(
        binder: Scenario,
        args: ObjectArguments,
        textUpdater: (Scenario, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val tint = args.string("tint") { textUpdater(binder, it) } ?: return
        val prop = binder.props[Key.key("model:$id")] as? ModelAnimationProp ?: return

        val tintValues = tint.split(";").mapNotNull { it.toIntOrNull() }
        if (tintValues.size != 3) {
            return
        }
        val tintColor = Color.fromRGB(tintValues[0], tintValues[1], tintValues[2])
        prop.setTint(tintColor)
    }
}