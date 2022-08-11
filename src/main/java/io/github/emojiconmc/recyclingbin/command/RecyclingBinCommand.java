package io.github.emojiconmc.recyclingbin.command;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import io.github.emojiconmc.recyclingbin.block.RecyclingBlock;
import io.github.emojiconmc.recyclingbin.file.LangFile;
import io.github.emojiconmc.recyclingbin.recycler.RecyclingMenu;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RecyclingBinCommand implements CommandExecutor {

    private RecyclingBinPlugin plugin;

    public RecyclingBinCommand(RecyclingBinPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        LangFile langFile = plugin.getLangFile();
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("open")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("recyclingbin.open")) {
                        new RecyclingMenu(plugin).open((Player) sender);
                    } else {
                        sender.sendMessage(langFile.getMessage("no-permission"));
                    }
                } else {
                    sender.sendMessage(langFile.getMessage("player-only"));
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("recyclingbin.reload")) {
                    plugin.reloadConfig();
                    plugin.getBlacklistManager().reload();
                    plugin.getLangFile().reload();
                    sender.sendMessage(langFile.getMessage("reloaded-files"));
                } else {
                    sender.sendMessage(langFile.getMessage("no-permission"));
                }
            } else if (args[0].equalsIgnoreCase("blacklist")) {
                if (sender.hasPermission("recyclingbin.blacklist")) {
                    if (args.length == 3) {
                        if (args[1].equalsIgnoreCase("add")) {
                            Material material = Material.getMaterial(args[2]);
                            if (material != null) {
                                List<String> blacklist = plugin.getBlacklistManager().getFileConfig().getStringList("blacklist");
                                blacklist.add(material.name());
                                plugin.getBlacklistManager().getFileConfig().set("blacklist", blacklist);
                                plugin.getBlacklistManager().save();
                                sender.sendMessage(langFile.getMessageWithPlaceholder("blacklist-add", "%material%", material.toString()));
                            } else {
                                sender.sendMessage(langFile.getMessage("invalid-blacklist-material"));
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            Material material = Material.getMaterial(args[2]);
                            if (material != null) {
                                List<String> blacklist = plugin.getBlacklistManager().getFileConfig().getStringList("blacklist");
                                if (blacklist.contains(material.name())) {
                                    blacklist.remove(material.name());
                                    plugin.getBlacklistManager().getFileConfig().set("blacklist", blacklist);
                                    plugin.getBlacklistManager().save();
                                    sender.sendMessage(langFile.getMessageWithPlaceholder("blacklist-remove", "%material%", material.toString()));
                                } else {
                                    sender.sendMessage(langFile.getMessage("invalid-blacklist-material-remove"));
                                }
                            } else {
                                sender.sendMessage(langFile.getMessage("invalid-blacklist-material-remove"));
                            }
                        } else {
                            sender.sendMessage(langFile.getMessage("improper-blacklist-usage"));
                        }
                    } else {
                        sender.sendMessage(langFile.getMessage("improper-blacklist-usage"));
                    }
                } else {
                    sender.sendMessage(langFile.getMessage("no-permission"));
                }
            } else if (args[0].equalsIgnoreCase("block")) {
                if (sender.hasPermission("recyclingbin.block.spawn")) {
                    if (sender instanceof Player) {
                        ((Player) sender).getInventory().addItem(RecyclingBlock.getInstance(plugin).getItemStack());
                        sender.sendMessage(langFile.getMessage("recieve-recycling-block"));
                    } else {
                        sender.sendMessage(langFile.getMessage("player-only"));
                    }
                } else {
                    sender.sendMessage(langFile.getMessage("no-permission"));
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("recyclingbin.help")) {
                    for (String helpMessage : langFile.getMessageList("recycling-bin-help")) {
                        sender.sendMessage(helpMessage);
                    }
                } else {
                    sender.sendMessage(langFile.getMessage("no-permission"));
                }
            } else {
                sender.sendMessage(langFile.getMessage("improper-usage"));
            }
        } else {
            sender.sendMessage(langFile.getMessage("plugin-info"));
        }

        return true;
    }
}
