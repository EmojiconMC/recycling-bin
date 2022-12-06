package io.github.emojiconmc.recyclingbin;

import io.github.emojiconmc.recyclingbin.block.RecyclingBlock;
import io.github.emojiconmc.recyclingbin.block.RecyclingBlockListener;
import io.github.emojiconmc.recyclingbin.command.RecycleCommand;
import io.github.emojiconmc.recyclingbin.command.RecyclingBinCommand;
import io.github.emojiconmc.recyclingbin.command.RecyclingBinTabCompleter;
import io.github.emojiconmc.recyclingbin.file.BlacklistFile;
import io.github.emojiconmc.recyclingbin.file.LangFile;
import io.github.emojiconmc.recyclingbin.menu.MenuListener;
import io.github.emojiconmc.recyclingbin.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RecyclingBinPlugin extends JavaPlugin {

    private BlacklistFile blacklistFile;
    private LangFile langFile;

    @Override
    public void onEnable() {
        getLogger().info("Recycling Bin has been enabled!");

        int pluginId = 16008;
        new Metrics(this, pluginId);
        blacklistFile = new BlacklistFile(this);
        langFile = new LangFile(this);
        saveDefaultConfig();

        if (getConfig().getBoolean("allow-craft-recycling-bin")) {
            RecyclingBlock.getInstance(this).initRecipe(this);
        }

        getCommand("recyclingbin").setExecutor(new RecyclingBinCommand(this));
        getCommand("recyclingbin").setTabCompleter(new RecyclingBinTabCompleter(this));
        getCommand("recycle").setExecutor(new RecycleCommand(this));

        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new RecyclingBlockListener(this), this);

        UpdateChecker updateChecker = new UpdateChecker(this);
        updateChecker.getLatestVersion(version -> {
            if (!updateChecker.getCurrentVersion().equalsIgnoreCase(version)) {
                getLogger().warning("Recycling Bin is on v" + updateChecker.getCurrentVersion() + " while the latest version is v"
                        + version + "! Update at https://www.spigotmc.org/resources/%E2%AD%90-recycling-bin-1-16-1-1-19-x.104332/");
            }
        });
    }

    @Override
    public void onDisable() {
        blacklistFile.save();
        langFile.save();
        getLogger().warning("Recycling Bin has been disabled!");
    }

    public BlacklistFile getBlacklistManager() {
        return blacklistFile;
    }

    public LangFile getLangFile() {
        return langFile;
    }
}
