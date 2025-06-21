package net.lielibrary.gui.buttons;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ButtonListener {
    void execute(InventoryClickEvent event);
}