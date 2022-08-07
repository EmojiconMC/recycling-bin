package io.github.emojiconmc.recyclingbin.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclingBinTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("open", "reload", "blacklist", "block", "help"), new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("blacklist")) {
            List<String> materials = new ArrayList<>();
            for (Material material : Material.values()) {
                materials.add(material.name());
            }

            return StringUtil.copyPartialMatches(args[1], materials, new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
