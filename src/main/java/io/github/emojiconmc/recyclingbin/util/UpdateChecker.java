package io.github.emojiconmc.recyclingbin.util;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private RecyclingBinPlugin plugin;
    private final int resourceId = 104332;

    public UpdateChecker(RecyclingBinPlugin plugin) {
        this.plugin = plugin;
    }

    public void getLatestVersion(Consumer<String> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId)
                            .openStream();
                    Scanner scanner = new Scanner(inputStream);
                    if (scanner.hasNext()) {
                        callback.accept(scanner.next());
                    }
                } catch (IOException e) {
                    plugin.getLogger().info("Cannot check for updates: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public String getCurrentVersion() {
        return plugin.getDescription().getVersion();
    }
}
