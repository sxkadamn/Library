package net.lielibrary.gui;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface CloseListener {
    void onClose(Player player);
}
