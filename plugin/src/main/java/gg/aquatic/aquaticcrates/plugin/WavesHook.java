package gg.aquatic.aquaticcrates.plugin;

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WavesHook {

    private static final String REQUIRED_VERSION = "1.3.17";

    private static Logger logger;
    /**
     * Checks if the Waves plugin is present and has the required version.
     *
     * @return true if Waves is properly installed, false otherwise
     */
    public static boolean check() {
        logger = AbstractCratesPlugin.INSTANCE.getLogger();

        Plugin wavesPlugin = Bukkit.getPluginManager().getPlugin("Waves");
        if (wavesPlugin == null) {

            logger.warning("Waves plugin not found! Required for AquaticCrates to function.");
            return false;
        }

        String version = wavesPlugin.getDescription().getVersion();
        if (!isVersionCompatible(version, REQUIRED_VERSION)) {
            logger.warning("Incompatible Waves version: " + version + ", required: " + REQUIRED_VERSION);
            return false;
        }

        logger.info("Waves detected (version " + version + ")");
        return true;
    }

    /**
     * Installs the Waves plugin from Spigot.
     */
    public static void install() {
        logger.info("Installing Waves dependency...");
        CompletableFuture.runAsync(() -> {
            try {
                installWaves();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to install Waves: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Downloads and installs the Waves plugin.
     *
     * @throws IOException if there is an error during download or installation
     */
    private static void installWaves() throws IOException, URISyntaxException {
        String targetFileName = "Waves-" + REQUIRED_VERSION + ".jar";
        File targetFile = new File(new File("plugins"), targetFileName);

        String url = "https://repo.nekroplex.com/releases/gg/aquatic/waves/Waves/"+REQUIRED_VERSION+"/Waves-"+REQUIRED_VERSION+"-plugin.jar";

        try {
            logger.info("Downloading Waves plugin from " + url + " to " + targetFile.getAbsolutePath() + "...");

            // Create URL and open connection
            URL downloadUrl = new URI(url).toURL();
            downloadUrl.openConnection();

            // Set up variables for progress tracking
            long totalSize = -1;
            long downloadedBytes = 0;
            int lastLoggedPercent = -1;
            byte[] buffer = new byte[8192]; // 8KB buffer
            long startTime = System.currentTimeMillis();

            // Create temporary file for download
            File tempFile = File.createTempFile("download-", ".tmp");
            tempFile.deleteOnExit();

            // Start download with progress tracking
            try (var input = downloadUrl.openStream();
                 var output = Files.newOutputStream(tempFile.toPath())) {

                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);

                    // Update progress
                    downloadedBytes += bytesRead;

                    // Log progress every 256KB
                    if (downloadedBytes / (256 * 1024) > lastLoggedPercent) {
                        logger.info("Downloaded: " + formatFileSize(downloadedBytes));
                        lastLoggedPercent = (int) (downloadedBytes / (256 * 1024));
                    }
                }
            }

            // Calculate final statistics
            double totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
            double averageSpeed = totalTime > 0 ? downloadedBytes / totalTime : 0.0;

            logger.info(
                    "Download completed successfully! " +
                            "Total size: " + formatFileSize(downloadedBytes) + ", " +
                            "Time: " + String.format("%.1f", totalTime) + "s, " +
                            "Average speed: " + formatFileSize((long) averageSpeed) + "/s"
            );

            // Move the temp file to the target location
            Files.move(
                    tempFile.toPath(),
                    targetFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            logger.info("Waves plugin installed successfully to " + targetFile.getAbsolutePath());
            logger.info("Restarting server to apply plugin...");
            Bukkit.getServer().shutdown();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to download Waves plugin: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Compares two version strings to determine if they are compatible.
     *
     * @param currentVersion the current version
     * @param requiredVersion the required version
     * @return true if the versions are compatible, false otherwise
     */
    private static boolean isVersionCompatible(String currentVersion, String requiredVersion) {
        // Simple version check - can be expanded for more complex version matching
        return currentVersion.startsWith(requiredVersion);
    }

    /**
     * Formats file size in bytes to a human-readable format.
     *
     * @param size file size in bytes
     * @return formatted string representation of the file size
     */
    private static String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.1f %s",
                size / Math.pow(1024, digitGroups),
                units[digitGroups]
        );
    }
}
