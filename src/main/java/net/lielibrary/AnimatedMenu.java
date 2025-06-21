package net.lielibrary;

import net.lielibrary.bukkit.Plugin;
import net.lielibrary.gui.CloseListener;
import net.lielibrary.gui.buttons.Button;
import net.lielibrary.gui.customize.AnimationDirection;
import net.lielibrary.gui.customize.AnimationStrategy;
import net.lielibrary.gui.customize.AnimationType;
import net.lielibrary.gui.customize.filler.FillGlassAnimation;
import net.lielibrary.gui.customize.filler.RandomFillAnimation;
import net.lielibrary.gui.customize.filler.WaveAnimation;
import net.lielibrary.gui.customize.filler.SpiralAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public abstract class AnimatedMenu {
    protected final Inventory inventory;
    protected final HashMap<Integer, Button> slots;
    protected final AnimationStrategy animationStrategy;
    protected final Material fillMaterial;
    protected final int speed;
    protected final Sound sound;
    protected final float soundPitch;
    protected final float soundVolume;
    protected final boolean animationEnabled;
    protected final AnimationDirection animationDirection;
    protected final int batchSize;
    protected final Queue<Integer> animationQueue;
    protected final Set<Integer> animatedSlots;
    private boolean interactDisabled = true;
    protected int currentPage = 0;
    protected final int itemsPerPage;
    protected final int rowsPerPage;
    protected final int navPrevSlot;
    protected final int navNextSlot;
    protected final List<Integer> contentSlots;
    protected final Button prevButton;
    protected final Button nextButton;
    protected BukkitTask autoUpdateTask;
    protected final boolean updateEnabled;
    protected CloseListener closeListener;
    private BukkitTask animationTask;
    private final List<Consumer<Player>> animationEffects;
    protected Particle particleEffect;
    protected int particleCount;

    public AnimatedMenu(String title, int rows, AnimationType animationType, Material fillMaterial, int speed,
                        Sound sound, float soundVolume, float soundPitch, boolean updateEnabled, FileConfiguration config,
                        int navPrevSlot, int navNextSlot) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.slots = new HashMap<>();
        this.animationStrategy = createAnimationStrategy(animationType);
        this.fillMaterial = fillMaterial != null ? fillMaterial : Material.GRAY_STAINED_GLASS_PANE;
        this.speed = Math.max(1, Math.min(speed, 20));
        this.sound = sound != null ? sound : Sound.BLOCK_NOTE_BLOCK_BANJO;
        this.soundVolume = Math.max(0.0f, Math.min(soundVolume, 2.0f));
        this.soundPitch = Math.max(0.5f, Math.min(soundPitch, 2.0f));
        this.animationQueue = new LinkedList<>();
        this.rowsPerPage = Math.max(1, Math.min(rows, 6));
        this.itemsPerPage = rowsPerPage * 9;
        this.navPrevSlot = navPrevSlot >= -1 && navPrevSlot < itemsPerPage ? navPrevSlot : itemsPerPage - 9;
        this.navNextSlot = navNextSlot >= -1 && navNextSlot < itemsPerPage ? navNextSlot : itemsPerPage - 1;
        this.contentSlots = new ArrayList<>();
        for (int i = 0; i < itemsPerPage; i++) {
            if (i != this.navPrevSlot && i != this.navNextSlot) {
                contentSlots.add(i);
            }
        }
        this.updateEnabled = updateEnabled;
        this.animationEffects = new ArrayList<>();
        this.animationEnabled = config.getBoolean("menu.animation.enabled", true);
        this.batchSize = Math.max(1, config.getInt("menu.animation.batch_size", 3));
        try {
            this.particleEffect = Particle.valueOf(config.getString("menu.animation.effects.particles", "SPELL_WITCH"));
        } catch (IllegalArgumentException e) {
            this.particleEffect = Particle.SPELL_WITCH;
        }
        this.particleCount = config.getInt("menu.animation.effects.particle_count");

        String directionStr = config.getString("menu.animation.direction", "FORWARD").toUpperCase();
        AnimationDirection tempDirection = AnimationDirection.FORWARD;
        try {
            tempDirection = AnimationDirection.valueOf(directionStr);
        } catch (IllegalArgumentException ignored) {
        }
        this.animationDirection = tempDirection;

        this.animatedSlots = new HashSet<>();

        ItemStack prevItem = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prevItem.getItemMeta();
        prevMeta.setDisplayName("Предыдущая");
        prevItem.setItemMeta(prevMeta);
        this.prevButton = new Button(prevItem, event -> {
            if (currentPage > 0) {
                setPage(currentPage - 1);
            }
        });

        ItemStack nextItem = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextItem.getItemMeta();
        nextMeta.setDisplayName("Следующая");
        nextItem.setItemMeta(nextMeta);
        this.nextButton = new Button(nextItem, event -> {
            int totalContentSlots = contentSlots.size();
            int totalPages = (int) Math.ceil((double) slots.size() / totalContentSlots);
            if (currentPage < totalPages - 1) {
                setPage(currentPage + 1);
            }
        });

        updatePage();

        if (updateEnabled) {
            startAutoUpdate();
        }
    }

    private AnimationStrategy createAnimationStrategy(AnimationType type) {
        return switch (type) {
            case WAVE -> new WaveAnimation();
            case RANDOM_FILL -> new RandomFillAnimation();
            case SPIRAL -> new SpiralAnimation();
            default -> new FillGlassAnimation();
        };
    }

    public void addAnimationEffect(Consumer<Player> effect) {
        this.animationEffects.add(effect);
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            inventory.clear();
            player.openInventory(inventory);
            disableInteract(true);

            if (animationEnabled) {
                startAnimation(player, animationDirection, null);
            } else {
                fillInventoryWithoutAnimation();
                disableInteract(false);
            }
        });
    }

    public void startAnimation(Player player, AnimationDirection direction, List<Integer> customSlots) {
        animationQueue.clear();
        animatedSlots.clear();

        List<Integer> slotsToAnimate = customSlots != null ? customSlots : animationStrategy.getAnimationSlots(inventory);
        switch (direction) {
            case BACKWARD -> {
                List<Integer> reversed = new ArrayList<>(slotsToAnimate);
                Collections.reverse(reversed);
                animationQueue.addAll(reversed);
            }
            case RANDOM -> {
                List<Integer> shuffled = new ArrayList<>(slotsToAnimate);
                Collections.shuffle(shuffled);
                animationQueue.addAll(shuffled);
            }
            case INWARD -> {
                List<Integer> inward = new ArrayList<>(slotsToAnimate);
                inward.sort((a, b) -> {
                    int center = inventory.getSize() / 2;
                    return Integer.compare(Math.abs(a - center), Math.abs(b - center));
                });
                animationQueue.addAll(inward);
            }
            case OUTWARD -> {
                List<Integer> outward = new ArrayList<>(slotsToAnimate);
                outward.sort((a, b) -> {
                    int center = inventory.getSize() / 2;
                    return Integer.compare(Math.abs(b - center), Math.abs(a - center));
                });
                animationQueue.addAll(outward);
            }
            default -> animationQueue.addAll(slotsToAnimate);
        }

        stopAnimation();

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.getOpenInventory().getTopInventory().equals(inventory)) {
                    stopAnimation();
                    return;
                }

                for (int i = 0; i < batchSize && !animationQueue.isEmpty(); i++) {
                    int slot = animationQueue.poll();
                    if (slots.containsKey(slot)) {
                        inventory.setItem(slot, slots.get(slot).getItem());
                    } else {
                        inventory.setItem(slot, new ItemStack(fillMaterial));
                        animatedSlots.add(slot);
                    }
                }

                if (animationQueue.isEmpty()) {
                    stopAnimation();
                    disableInteract(false);
                    return;
                }

                player.playSound(player.getLocation(), sound, soundVolume, soundPitch);
                animationEffects.forEach(effect -> effect.accept(player));
                player.updateInventory();
            }
        }.runTaskTimer(Plugin.getLibrary(), 0L, speed);
    }

    private void displayParticleEffect(Player player, int slot) {
        int row = slot / 9;
        int col = slot % 9;
        double x = player.getLocation().getX() + (col - 4) * 0.3;
        double y = player.getLocation().getY() + 1.5 + (3 - row) * 0.3;
        double z = player.getLocation().getZ() + 0.5;
        player.getWorld().spawnParticle(particleEffect, x, y, z, particleCount, 0.1, 0.1, 0.1, 0.0);
    }

    private void fillInventoryWithoutAnimation() {
        List<Integer> slotsToFill = animationStrategy.getAnimationSlots(inventory);
        for (int slot : slotsToFill) {
            if (slots.containsKey(slot)) {
                inventory.setItem(slot, slots.get(slot).getItem());
            } else {
                inventory.setItem(slot, new ItemStack(fillMaterial));
            }
        }
    }

    public void clearWithAnimation(Player player, AnimationDirection direction, List<Integer> customSlots) {
        animationQueue.clear();
        animatedSlots.clear();

        List<Integer> slotsToClear = customSlots != null ? customSlots : animationStrategy.getAnimationSlots(inventory);
        switch (direction) {
            case BACKWARD -> {
                List<Integer> reversed = new ArrayList<>(slotsToClear);
                Collections.reverse(reversed);
                animationQueue.addAll(reversed);
            }
            case RANDOM -> {
                List<Integer> shuffled = new ArrayList<>(slotsToClear);
                Collections.shuffle(shuffled);
                animationQueue.addAll(shuffled);
            }
            case INWARD -> {
                List<Integer> inward = new ArrayList<>(slotsToClear);
                inward.sort((a, b) -> {
                    int center = inventory.getSize() / 2;
                    return Integer.compare(Math.abs(a - center), Math.abs(b - center));
                });
                animationQueue.addAll(inward);
            }
            case OUTWARD -> {
                List<Integer> outward = new ArrayList<>(slotsToClear);
                outward.sort((a, b) -> {
                    int center = inventory.getSize() / 2;
                    return Integer.compare(Math.abs(b - center), Math.abs(a - center));
                });
                animationQueue.addAll(outward);
            }
            default -> animationQueue.addAll(slotsToClear);
        }

        stopAnimation();

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.getOpenInventory().getTopInventory().equals(inventory)) {
                    stopAnimation();
                    return;
                }

                for (int i = 0; i < batchSize && !animationQueue.isEmpty(); i++) {
                    int slot = animationQueue.poll();
                    inventory.clear(slot);
                    animatedSlots.remove(slot);
                }

                if (animationQueue.isEmpty()) {
                    stopAnimation();
                    return;
                }

                player.playSound(player.getLocation(), sound, soundVolume, soundPitch);
                animationEffects.forEach(effect -> effect.accept(player));
                player.updateInventory();
            }
        }.runTaskTimer(Plugin.getLibrary(), 0L, speed);
    }

    public void stopAnimation() {
        if (animationTask != null) {
            animationTask.cancel();
            animationTask = null;
        }

        Bukkit.getScheduler().runTask(Plugin.getLibrary(), () -> {
            for (int slot : animatedSlots) {
                if (!slots.containsKey(slot)) {
                    inventory.clear(slot);
                }
            }
            animatedSlots.clear();
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
        if (position != navPrevSlot && position != navNextSlot) {
            slot.setPosition(position);
            slots.put(position, slot);
            inventory.setItem(position, slot.getItem());
        }
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
            int totalContentSlots = contentSlots.size();
            int totalPages = (int) Math.ceil((double) slots.size() / totalContentSlots);
            int start = currentPage * totalContentSlots;
            int end = Math.min(start + totalContentSlots, slots.size());

            List<Map.Entry<Integer, Button>> entries = new ArrayList<>(slots.entrySet());
            for (int j = start; j < end && j < entries.size(); j++) {
                Map.Entry<Integer, Button> entry = entries.get(j);
                int index = j - start;
                int inventorySlot = contentSlots.get(index);
                inventory.setItem(inventorySlot, entry.getValue().getItem());
            }

            if (navPrevSlot != -1) {
                if (currentPage > 0) {
                    ItemStack prevItem = prevButton.getItem().clone();
                    ItemMeta meta = prevItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add("Перейти на страницу " + currentPage);
                    meta.setLore(lore);
                    prevItem.setItemMeta(meta);
                    inventory.setItem(navPrevSlot, prevItem);
                } else {
                    inventory.setItem(navPrevSlot, new ItemStack(fillMaterial));
                }
            }

            if (navNextSlot != -1) {
                if (currentPage < totalPages - 1) {
                    ItemStack nextItem = nextButton.getItem().clone();
                    ItemMeta meta = nextItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add("Перейти на страницу " + (currentPage + 2));
                    meta.setLore(lore);
                    nextItem.setItemMeta(meta);
                    inventory.setItem(navNextSlot, nextItem);
                } else {
                    inventory.setItem(navNextSlot, new ItemStack(fillMaterial));
                }
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

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getSoundPitch() {
        return soundPitch;
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
        this.currentPage = Math.max(0, Math.min(page, (int) Math.ceil((double) slots.size() / contentSlots.size()) - 1));
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

    public int getNavPrevSlot() {
        return navPrevSlot;
    }

    public int getNavNextSlot() {
        return navNextSlot;
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
        if (this.autoUpdateTask != null) {
            this.autoUpdateTask.cancel();
            this.autoUpdateTask = null;
        }
    }
}