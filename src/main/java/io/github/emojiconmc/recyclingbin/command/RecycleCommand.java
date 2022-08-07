package io.github.emojiconmc.recyclingbin.command;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import io.github.emojiconmc.recyclingbin.recycler.RecyclingMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecycleCommand implements CommandExecutor {

    private RecyclingBinPlugin plugin;

    public RecycleCommand(RecyclingBinPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("recyclingbin.open")) {
                new RecyclingMenu(plugin).open((Player) sender);
            } else {
                sender.sendMessage(plugin.getLangFile().getMessage("no-permission"));
            }
        } else {
            sender.sendMessage(plugin.getLangFile().getMessage("player-only"));
        }

        return false;
    }
}
