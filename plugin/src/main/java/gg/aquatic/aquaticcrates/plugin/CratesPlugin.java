package gg.aquatic.aquaticcrates.plugin;

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin;
import gg.aquatic.aquaticcrates.api.PluginSettings;
import gg.aquatic.aquaticcrates.api.crate.CrateHandler;
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate;
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate;
import gg.aquatic.aquaticcrates.plugin.log.LogMenuSettings;
import gg.aquatic.aquaticcrates.plugin.reward.menu.RewardsMenuSettings;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CratesPlugin extends AbstractCratesPlugin {

    // Static instance getter
    public static CratesPlugin getInstance() {
        return (CratesPlugin) AbstractCratesPlugin.INSTANCE;
    }

    // Properties
    private PluginSettings settings;
    private boolean loading = true;
    private RewardsMenuSettings rewardsMenuSettings;
    private LogMenuSettings logMenuSettings;

    @Override
    public void onLoad() {
        AbstractCratesPlugin.INSTANCE = this;
        if (!WavesHook.check()) {
            WavesHook.install();
            return;
        }

        Bootstrap.INSTANCE.onLoad$plugin();
    }

    @Override
    public void onEnable() {
        Bootstrap.INSTANCE.enable$plugin();
    }

    public CompletableFuture<Boolean> reloadPlugin() {
        if (loading) {
            return CompletableFuture.completedFuture(false);
        }

        for (Object value : CrateHandler.INSTANCE.getCrates().values()) {
            if (value instanceof OpenableCrate) {
                ((OpenableCrate) value).getAnimationManager().forceStopAllAnimations();
            }
        }

        CrateHandler.INSTANCE.getCrates().clear();

        for (SpawnedCrate value : CrateHandler.INSTANCE.getSpawned().values()) {
            value.destroy();
        }

        CrateHandler.INSTANCE.getSpawned().clear();

        return Bootstrap.INSTANCE.load$plugin().thenApply(v -> true);
    }

    // Getters and setters
    @Override
    public @NotNull PluginSettings getSettings() {
        return settings;
    }

    public void setSettings(PluginSettings settings) {
        this.settings = settings;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public RewardsMenuSettings getRewardsMenuSettings() {
        return rewardsMenuSettings;
    }

    public void setRewardsMenuSettings(RewardsMenuSettings rewardsMenuSettings) {
        this.rewardsMenuSettings = rewardsMenuSettings;
    }

    public LogMenuSettings getLogMenuSettings() {
        return logMenuSettings;
    }

    public void setLogMenuSettings(LogMenuSettings logMenuSettings) {
        this.logMenuSettings = logMenuSettings;
    }

}
