package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.util.Config
import org.bukkit.configuration.ConfigurationSection
import java.io.File

object CrateSerializer {

    fun loadCrates(): HashMap<String, Crate> {
        CratesPlugin.INSTANCE.dataFolder.mkdirs()
        val crates = HashMap<String, Crate>()

        val basicFolder = File(CratesPlugin.INSTANCE.dataFolder, "regularcrates")
        basicFolder.mkdirs()

        crates += loadBasicCrates(basicFolder)

        return crates
    }

    fun loadBasicCrates(folder: File): HashMap<String, BasicCrate> {
        val crates = HashMap<String, BasicCrate>()
        for (file in folder.listFiles()!!) {
            if (file.isDirectory) {
                crates += loadBasicCrates(file)
                continue
            }
            val c = loadBasicCrate(file) ?: continue
            crates[c.identifier] = c
        }
        return crates
    }
    fun loadBasicCrate(file: File): BasicCrate? {
        val identifier = file.nameWithoutExtension
        val config = Config(file, CratesPlugin.INSTANCE)
        config.load()
        val cfg = config.getConfiguration()

        return null
    }

    private fun loadRewards(): HashMap<String, Reward>  {
        val rewards = HashMap<String, Reward>()
        return rewards
    }

    private fun loadReward(section: ConfigurationSection): Reward? {
        return null
    }

}