package net.lielibrary;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.CloseListener;
import net.lielibrary.gui.buttons.Button;
import net.lielibrary.gui.customize.AnimationStrategy;
import net.lielibrary.gui.customize.AnimationType;
import net.lielibrary.gui.customize.filler.FillGlassAnimation;
import net.lielibrary.gui.customize.filler.RandomFillAnimation;
import net.lielibrary.gui.customize.filler.WaveAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AnimatedMenu {
    protected final Inventory inventory;
    protected final HashMap<Integer, Button> slots;
    protected final AnimationStrategy animationStrategy;
    protected final Material fillMaterial;
    protected final int speed;
    protected final Sound sound;
    protected final Queue<Integer> animationQueue;
    protected final Set<Integer> animatedSlots;
    private boolean interactDisabled = true;
    protected int currentPage = 0;
    protected final int itemsPerPage;
    protected final int rowsPerPage;
    protected final ItemStack prevPageItem;
    protected final ItemStack nextPageItem;
    protected @NotNull BukkitTask autoUpdateTask;
    protected final boolean updateEnabled;
    protected CloseListener closeListener;

    public AnimatedMenu(String title, int rows, AnimationType animationType, Material fillMaterial, int speed, Sound sound, boolean updateEnabled) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.slots = new HashMap<>();
        this.animationStrategy = createAnimationStrategy(animationType);
        this.fillMaterial = fillMaterial;
        this.speed = speed;
        this.sound = sound;
        this.animationQueue = new LinkedList<>();
        this.rowsPerPage = rows;
        this.itemsPerPage = rows * 9;
        this.prevPageItem = new ItemStack(Material.ARROW);
        this.nextPageItem = new ItemStack(Material.ARROW);
        this.updateEnabled = updateEnabled;
        updatePage();

        if (updateEnabled) {
            startAutoUpdate();
        }
        this.animatedSlots = new HashSet<>();
    }

    private AnimationStrategy createAnimationStrategy(AnimationType type) {
        return switch (type) {
            case WAVE -> new WaveAnimation();
            case RANDOM_FILL -> new RandomFillAnimation();
            default -> new FillGlassAnimation();
        };
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            inventory.clear();
            player.openInventory(inventory);
            disableInteract(true);
            startAnimation(player);
        });
    }

    private void startAnimation(Player player) {
        animationQueue.addAll(animationStrategy.getAnimationSlots(inventory));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (animationQueue.isEmpty()) {
                    cancel();
                    removeAnimation(player);
                    return;
                }

                int slot = animationQueue.poll();

                if (slots.containsKey(slot)) {
                    inventory.setItem(slot, slots.get(slot).getItem());
                } else {
                    inventory.setItem(slot, new ItemStack(fillMaterial));
                    animatedSlots.add(slot);
                }

                player.playSound(player.getLocation(), sound, 1.5F, 1.5F);
                player.updateInventory();
            }
        }.runTaskTimer(Plugin.getLibrary(), 0L, speed);
    }

    private void removeAnimation(Player player) {
        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            for (int slot : animatedSlots) {
                if (!slots.containsKey(slot)) {
                    inventory.clear(slot);
                }
            }
            animatedSlots.clear();
            player.updateInventory();
            disableInteract(false);
        });
    }

    public AnimatedMenu refreshItems() {
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

    public AnimatedMenu refreshSlot(int slot) {
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

    public AnimatedMenu setSlot(int position, Button slot) {
        slot.setPosition(position);
        slots.put(position, slot);
        inventory.setItem(position, slot.getItem());
        return this;
    }

    public AnimatedMenu disableInteract(boolean disable) {
        interactDisabled = disable;
        return this;
    }

    public boolean isInteractDisabled() {
        return interactDisabled;
    }

    public AnimatedMenu removeSlot(int position) {
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



    public boolean hasCloseListener() {
        return closeListener != null;
    }

    public CloseListener getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public Sound getSound() {
        return sound;
    }


    public Inventory getInventory() {
        return inventory;
    }

    public HashMap<Integer, Button> getSlots() {
        return slots;
    }

    public AnimationStrategy getAnimationStrategy() {
        return animationStrategy;
    }

    public Material getFillMaterial() {
        return fillMaterial;
    }

    public int getSpeed() {
        return speed;
    }

    public void setPage(int page) {
        this.currentPage = page;
        updatePage();
    }

    public int getCurrentPage() {
        return currentPage;
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

    public @NotNull BukkitTask getAutoUpdateTask() {
        return autoUpdateTask;
    }

    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    public Button getSlot(int n) {
        return slots.get(n);
    }

    private void startAutoUpdate() {
        autoUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTask(Plugin.getLibrary(), AnimatedMenu.this::refreshItems);
            }
        }.runTaskTimer(Plugin.getLibrary(), 0L, 60L);
    }

    public void stopAutoUpdate() {
        if(autoUpdateTask == null) return;

        this.autoUpdateTask.cancel();
    }
}