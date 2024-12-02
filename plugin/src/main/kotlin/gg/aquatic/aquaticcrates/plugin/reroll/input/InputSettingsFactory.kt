package gg.aquatic.aquaticcrates.plugin.reroll.input

import gg.aquatic.aquaticcrates.api.reroll.RerollInput
import org.bukkit.configuration.file.FileConfiguration

interface InputSettingsFactory {

    fun serialize(cfg: FileConfiguration): RerollInput?

}