package io.github.emojiconmc.recyclingbin.command;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclingBinTabCompleter implements TabCompleter {

    private RecyclingBinPlugin plugin;

    public RecyclingBinTabCompleter(RecyclingBinPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("open", "reload", "blacklist", "block", "help"),
                    new ArrayList<>());
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("blacklist")) {
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("add", "remove"), new ArrayList<>());
            } else if (args.length == 3) {
                if (args[1].equalsIgnoreCase("add")) {
                    List<String> materials = new ArrayList<>();
                    for (Material material : Material.values()) {
                        materials.add(material.name());
                    }

                    return StringUtil.copyPartialMatches(args[2], materials, new ArrayList<>());
                } else if (args[1].equalsIgnoreCase("remove")) {
                    List<String> blacklist = plugin.getBlacklistManager().getFileConfig().getStringList("blacklist");
                    return StringUtil.copyPartialMatches(args[2], blacklist, new ArrayList<>());
                }
            }
        }

        return new ArrayList<>();
    }
}
