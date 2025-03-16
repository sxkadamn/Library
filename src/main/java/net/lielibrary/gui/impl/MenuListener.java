package net.lielibrary.gui.impl;


import net.lielibrary.AnimatedMenu;
import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.buttons.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        if (menu != null && menu.hasCloseListener()) {
            menu.getCloseListener().onClose((Player) event.getPlayer());


            menu.stopAutoUpdate();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.NOTHING || event.isCancelled()) return;

        Inventory clicked = event.getInventory();
        AnimatedMenu menu = Plugin.getMenuManager().getMenu(clicked);

        if (menu != null) {
            if (menu.isInteractDisabled()) {
                event.setCancelled(true);
                return;
            }

            Button button = menu.getSlot(event.getSlot());
            if (button != null) {
                if (button.isInteractDisabled() &&
                        event.getClickedInventory().equals(event.getView().getTopInventory())) {
                    event.setCancelled(true);
                }

                button.executeListener(event);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.isSimilar(menu.getPrevPageItem())) {
                    menu.setPage(menu.getCurrentPage() - 1);
                } else if (clickedItem != null && clickedItem.isSimilar(menu.getNextPageItem())) {
                    menu.setPage(menu.getCurrentPage() + 1);
                }
            }
        }
    }
}