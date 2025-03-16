package net.lielibrary.bukkit.requirements.commands;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.bukkit.requirements.RequireHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class BroadcastCommand implements RequireHandler {
    private final String message;

    public BroadcastCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute(Player player) {
        Bukkit.broadcast(new TextComponent(Plugin.getWithColor().hexToMinecraftColor(message)));
    }
}
