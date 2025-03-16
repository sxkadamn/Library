package net.lielibrary.bukkit.requirements.commands;

import net.lielibrary.bukkit.requirements.RequireHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class  ConsoleCommand implements RequireHandler {
    private final String command;

    public ConsoleCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player) {
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command
                .replace("{player_name}", player.getName()));

    }
}