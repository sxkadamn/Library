package net.lielibrary.bukkit.requirements.commands;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.bukkit.requirements.RequireHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarCommand implements RequireHandler {
    private final String message;

    public ActionBarCommand(String message) {
        this.message = Plugin.getWithColor().hexToMinecraftColor(message);
    }

    @Override
    public void execute(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
