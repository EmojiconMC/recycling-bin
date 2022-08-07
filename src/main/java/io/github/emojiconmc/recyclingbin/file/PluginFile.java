package io.github.emojiconmc.recyclingbin.file;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class PluginFile {

    private String name;
    private File file;
    private FileConfiguration fileConfig;

    public PluginFile(RecyclingBinPlugin plugin, String name) {
        this.name = name;
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        reload();
    }

    public void save() {
        try {
            fileConfig.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getFileConfig() {
        return fileConfig;
    }
}
