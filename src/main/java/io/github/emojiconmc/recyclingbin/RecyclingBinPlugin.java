package io.github.emojiconmc.recyclingbin;

import io.github.emojiconmc.recyclingbin.block.RecyclingBlock;
import io.github.emojiconmc.recyclingbin.block.RecyclingBlockListener;
import io.github.emojiconmc.recyclingbin.command.RecycleCommand;
import io.github.emojiconmc.recyclingbin.command.RecyclingBinCommand;
import io.github.emojiconmc.recyclingbin.command.RecyclingBinTabCompleter;
import io.github.emojiconmc.recyclingbin.file.BlacklistFile;
import io.github.emojiconmc.recyclingbin.file.LangFile;
import io.github.emojiconmc.recyclingbin.menu.MenuListener;
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
        getCommand("recyclingbin").setTabCompleter(new RecyclingBinTabCompleter());
        getCommand("recycle").setExecutor(new RecycleCommand(this));

        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new RecyclingBlockListener(this), this);
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
