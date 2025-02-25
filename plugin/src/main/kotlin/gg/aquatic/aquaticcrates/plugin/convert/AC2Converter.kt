package gg.aquatic.aquaticcrates.plugin.convert

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration

object AC2Converter: Converter {
    override fun convert(config: Config) {
        val cfg = config.getConfiguration()!!

        val folder = CratesPlugin.INSTANCE.dataFolder.resolve("converted")
        folder.mkdirs()

        val newConfig = Config(folder.resolve(config.getFile().name), CratesPlugin.INSTANCE)
        newConfig.load()
        val newCfg = newConfig.getConfiguration()!!

        newCfg.set("display-name", cfg.getString("display-name"))
        newCfg.set("key", cfg.getConfigurationSection("key"))

        newCfg.set("key.requires-crate-to-open", null)

        if (!cfg.getBoolean("requires-crate-to-open")) {
            newCfg.set("key.interaction.RIGHT.type", "CRATE_OPEN")
        }
        cfg.getConfigurationSection("visual")?.let { visualSection ->
            val type = visualSection.getString("type")!!
            val sections = ArrayList<ConfigurationSection>()
            when (type.lowercase()) {
                "modelengine" -> {
                    val section = MemoryConfiguration()
                    section.set("model", cfg.getString("visual.model"))
                    section.set("type", "MODELENGINE")

                    sections.add(section)
                }
            }
            newCfg.set("interactables", sections)
        }

        cfg.getConfigurationSection("hologram")?.let { hologramSection ->
            newCfg.set("offset", hologramSection.getString("offset","0;0;0"))

            val sections = ArrayList<ConfigurationSection>()
            for (lineSection in hologramSection.getSectionList("lines")) {
                if (lineSection.getString("type") == "TEXT_DISPLAY") {
                    sections.add(lineSection)
                    lineSection.set("type", "TEXT")
                }
            }
        }

        cfg.getConfigurationSection("rewards")?.let { rewardsSection ->
            rewardsSection.getKeys(false).forEach { key ->
                val rewardSection = rewardsSection.getConfigurationSection(key)!!
                for (setting in rewardSection.getKeys(false)) {
                    if (setting.lowercase() == "hologram") continue
                    val value = rewardSection.get(setting)!!
                    newCfg.set("rewards.$key.$setting", value)
                }
            }
        }

        cfg.getConfigurationSection("preview")?.let { previewSection ->
            if (previewSection.getBoolean("openable-using-key")) {
                newCfg.set("key.interaction.LEFT.type", "PREVIEW-CRATE")
            }

            for (option in previewSection.getKeys(false)) {
                if (option.lowercase() == "openable-using-key") continue
                val value = previewSection.get(option)!!
                newCfg.set("preview.$option", value)
            }
        }

        newConfig.save()
    }
}