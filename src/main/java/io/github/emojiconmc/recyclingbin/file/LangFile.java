package io.github.emojiconmc.recyclingbin.file;

import io.github.emojiconmc.recyclingbin.RecyclingBinPlugin;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class LangFile extends PluginFile {

    public LangFile(RecyclingBinPlugin plugin) {
        super(plugin, "lang.yml");
    }

    public String getMessage(String configPath) {
        String message = getFileConfig().getString(configPath);
        if (message == null) {
            return "";
        }

        StringBuilder finalMessage = new StringBuilder();
        String prefix = getFileConfig().getString("prefix");
        if (prefix != null) {
            finalMessage.append(prefix).append(" ");
        }

        finalMessage.append(message);
        return ChatColor.translateAlternateColorCodes('&', finalMessage.toString());
    }

    public String getMessageWithPlaceholder(String configPath, String placeholder, String replaceWith) {
        return getMessage(configPath).replaceAll(placeholder, replaceWith);
    }

    public List<String> getMessageList(String configPath) {
        List<String> messages = new ArrayList<>();
        for (String message : getFileConfig().getStringList(configPath)) {
            messages.add(ChatColor.translateAlternateColorCodes('&', message));
        }

        return messages;
    }
}
