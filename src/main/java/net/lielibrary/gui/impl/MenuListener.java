package net.lielibrary.gui.impl;

import net.lielibrary.AnimatedMenu;
import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.buttons.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        Inventory clicked = event.getView().getTopInventory();
        AnimatedMenu menu = Plugin.getMenuManager().getMenu(clicked);

        if (menu != null && menu.isInteractDisabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory closed = event.getInventory();
        AnimatedMenu menu = Plugin.getMenuManager().getMenu(closed);

        if (menu != null) {
            menu.stopAnimation();
            menu.stopAutoUpdate();

            if (menu.hasCloseListener()) {
                menu.getCloseListener().onClose((Player) event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.NOTHING || event.getClickedInventory() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        AnimatedMenu menu = Plugin.getMenuManager().getMenu(topInventory);

        if (menu == null) {
            return;
        }

        if (clickedInventory.equals(topInventory)) {
            event.setCancelled(true);

            int slot = event.getSlot();
            Button button = menu.getSlot(slot);

            if (slot == menu.getNavPrevSlot() || slot == menu.getNavNextSlot()) {
                if (button != null && !button.isInteractDisabled()) {
                    button.executeListener(event);
                }
                return;
            }
            if (button != null && !button.isInteractDisabled()) {
                button.executeListener(event);
            }
        } else {
            if (menu.isInteractDisabled()) {
                event.setCancelled(true);
            }
        }
    }
}