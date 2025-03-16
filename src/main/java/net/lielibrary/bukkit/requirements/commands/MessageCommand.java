package net.lielibrary.bukkit.requirements.commands;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.bukkit.requirements.RequireHandler;
import org.bukkit.entity.Player;

public class MessageCommand implements RequireHandler {
    private final String message;

    public MessageCommand(String message) {
        this.message = Plugin.getWithColor().hexToMinecraftColor(message);
    }

    @Override
    public void execute(Player player) {
        player.sendMessage(message);
    }
}