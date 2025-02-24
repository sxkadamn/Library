package net.lielibrary.gui;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Menu {

    private final String id;
    private final String title;
    private Inventory inventory;
    private final HashMap<Integer, Button> slots;
    private boolean interactDisabled = true;
    private CloseListener closeListener;
    private int currentPage = 0;
    private final int itemsPerPage;
    private final int rowsPerPage;
    private final ItemStack prevPageItem;
    private final ItemStack nextPageItem;
    private @NotNull BukkitTask autoUpdateTask;
    private final boolean updateEnabled;


    public Menu(String id, String title, int rows, boolean updateEnabled) {
        this.id = id;
        this.title = title;
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.slots = new HashMap<>();
        this.rowsPerPage = rows;
        this.itemsPerPage = rows * 9;
        this.prevPageItem = new ItemStack(Material.ARROW);
        this.nextPageItem = new ItemStack(Material.ARROW);
        this.updateEnabled = updateEnabled;
        updatePage();

        if (updateEnabled) {
            startAutoUpdate();
        }
    }

    public Menu(String id, InventoryHolder holder, String title, InventoryType type, boolean updateEnabled) {
        this.id = id;
        this.title = title;
        this.inventory = Bukkit.createInventory(holder, type, title);
        this.slots = new HashMap<>();
        this.rowsPerPage = type.getDefaultSize() / 9;
        this.itemsPerPage = type.getDefaultSize();
        this.prevPageItem = new ItemStack(Material.ARROW);
        this.nextPageItem = new ItemStack(Material.ARROW);
        this.updateEnabled = updateEnabled;
        updatePage();

        if (updateEnabled) {
            startAutoUpdate();
        }
    }

    public Menu setSlot(int position, Button slot) {
        slot.setPosition(position);
        slots.put(position, slot);
        inventory.setItem(position, slot.getItem());
        return this;
    }

    public Menu disableInteract(boolean disable) {
        interactDisabled = disable;
        return this;
    }

    public boolean isInteractDisabled() {
        return interactDisabled;
    }

    public Menu removeSlot(int position) {
        slots.remove(position);
        inventory.clear(position);
        return this;
    }

    private void updatePage() {
        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            inventory.clear();
            int start = currentPage * itemsPerPage;
            int end = Math.min(start + itemsPerPage, slots.size());

            for (int i = start; i < end; i++) {
                Button button = slots.get(i);
                if (button != null) {
                    inventory.setItem(i % itemsPerPage, button.getItem());
                }
            }

            if (currentPage > 0) {
                inventory.setItem(itemsPerPage - 9, prevPageItem);
            }
            if (end < slots.size()) {
                inventory.setItem(itemsPerPage - 1, nextPageItem);
            }

            for (HumanEntity player : inventory.getViewers()) {
                ((Player) player).updateInventory();
            }
        });
    }

    public Menu refreshItems() {
        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            inventory.clear();
            for (Map.Entry<Integer, Button> entry : slots.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue().getItem());
            }
            for (HumanEntity player : inventory.getViewers()) {
                ((Player) player).updateInventory();
            }
        });
        return this;
    }

    public Menu refreshSlot(int slot) {
        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            if (!slots.containsKey(slot)) {
                inventory.clear(slot);
            } else {
                inventory.setItem(slot, slots.get(slot).getItem());
            }
            for (HumanEntity player : inventory.getViewers()) {
                ((Player) player).updateInventory();
            }
        });
        return this;
    }

    public int getSlotPosition(Button slot) {
        for (Map.Entry<Integer, Button> entry : slots.entrySet()) {
            if (entry.getValue().equals(slot)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public String getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Deprecated
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        slots.clear();
    }

    public Button getSlot(int position) {
        return slots.get(position);
    }

    public boolean hasSlot(int slot) {
        return slots.containsKey(slot);
    }

    public boolean hasCloseListener() {
        return closeListener != null;
    }

    public CloseListener getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setPage(int page) {
        this.currentPage = page;
        updatePage();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public HashMap<Integer, Button> getSlots() {
        return slots;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public ItemStack getPrevPageItem() {
        return prevPageItem;
    }

    public ItemStack getNextPageItem() {
        return nextPageItem;
    }

    public String getId() {
        return id;
    }

    public @NotNull BukkitTask getAutoUpdateTask() {
        return autoUpdateTask;
    }

    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    private void startAutoUpdate() {
        autoUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTask(Plugin.getLibrary(), Menu.this::refreshItems);
            }
        }.runTaskTimer(Plugin.getLibrary(), 0L, 60L);
    }

    public void stopAutoUpdate() {
        if(autoUpdateTask == null) return;

        this.autoUpdateTask.cancel();
    }

}




