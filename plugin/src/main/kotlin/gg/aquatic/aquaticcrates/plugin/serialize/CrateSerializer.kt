package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.util.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.configuration.ConfigurationSection
import java.io.File

object CrateSerializer {

    suspend fun loadCrates(): HashMap<String, Crate> = withContext(Dispatchers.IO) {
        CratesPlugin.INSTANCE.dataFolder.mkdirs()
        val crates = HashMap<String, Crate>()

        val basicFolder = File(CratesPlugin.INSTANCE.dataFolder, "regularcrates")
        basicFolder.mkdirs()

        crates += loadBasicCrates(basicFolder)

        return@withContext crates
    }

    suspend fun loadBasicCrates(folder: File): HashMap<String, BasicCrate> = withContext(Dispatchers.IO) {
        val crates = HashMap<String, BasicCrate>()
        for (file in folder.listFiles()!!) {
            if (file.isDirectory) {
                crates += loadBasicCrates(file)
                continue
            }
            val c = loadBasicCrate(file) ?: continue
            crates[c.identifier] = c
        }
        return@withContext crates
    }
    suspend fun loadBasicCrate(file: File): BasicCrate? = withContext(Dispatchers.IO) {
        val identifier = file.nameWithoutExtension
        val config = Config(file, CratesPlugin.INSTANCE)
        config.load()
        val cfg = config.getConfiguration()

        null
    }

    private suspend fun loadRewards(): HashMap<String, Reward> = withContext(Dispatchers.IO) {
        val rewards = HashMap<String, Reward>()
        return@withContext rewards
    }

    private suspend fun loadReward(section: ConfigurationSection): Reward? = withContext(Dispatchers.IO) {
        return@withContext null
    }

}