package net.lielibrary.bukkit.requirements;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class RequirementAPI {
    private static FileConfiguration config;

    public static void loadConfig(FileConfiguration fileConfig) {
        config = fileConfig;
    }

    public static void executeFromConfig(Player player, String path) {
        if (config == null) {
            throw new IllegalStateException("Config not loaded! Use RequirementAPI.loadConfig() first");
        }

        List<String> commands = config.getStringList(path);
        if (commands.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Нет команд в пути: " + path);
            return;
        }

        RequirementExecute.execute(commands, player);
    }
}
