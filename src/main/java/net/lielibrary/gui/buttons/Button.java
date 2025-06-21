package net.lielibrary.gui.buttons;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.lielibrary.gui.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class Button {
    private ItemStack item;
    private ButtonListener listener;
    private boolean interactDisabled;
    private int position;
    private boolean staticated;
    private int clickCount = 0;
    private String dynamicDataType;
    private String displayFormat;

    public Button(ItemStack item, ButtonListener listener) {
        this.item = item != null ? item : new ItemStack(Material.AIR);
        this.listener = listener;
    }

    public Button(Material material) {
        this.item = new ItemStack(material);
    }

    public Button(Material material, Text display, List<Text> lore) {
        this(material);
        setDisplay(display);
        setLore(lore);
    }

    public Button(ItemStack item) {
        this.item = item != null ? item : new ItemStack(Material.AIR);
    }

    public Button setPlayerOwner(String playerName) {
        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
            item.setItemMeta(meta);
        }
        return this;
    }

    public Button setPlayerOwner(Player player) {
        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(player);
            item.setItemMeta(meta);
        }
        return this;
    }

    public Button setStatic(boolean staticated) {
        this.staticated = staticated;
        return this;
    }

    public boolean isStatic() {
        return staticated;
    }

    public ItemStack getItem() {
        return item;
    }

    public Button setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public Button setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public Button setDisplay(Text display) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display.getRaw());
        item.setItemMeta(meta);
        return this;
    }

    public Button setDisplay(String display) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return this;
    }

    public Button setLore(List<Text> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> loreList = new ArrayList<>();
        for (Text text : lore) {
            loreList.add(text.getRaw());
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return this;
    }

    public Button setLore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return this;
    }

    public Button setLoreList(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> loreList = new ArrayList<>();
        for (String text : lore) {
            loreList.add(text);
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return this;
    }

    public Button hideAttributes(boolean hide) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (hide) {
                meta.addItemFlags(ItemFlag.values());
            } else {
                meta.removeItemFlags(ItemFlag.values());
            }
            item.setItemMeta(meta);
        }
        return this;
    }

    public Button setEnchanted(boolean enchanted) {
        if (enchanted) {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        } else {
            item.removeEnchantment(Enchantment.LURE);
        }
        hideAttributes(enchanted);
        return this;
    }

    public Button disableInteract(boolean disable) {
        this.interactDisabled = disable;
        return this;
    }

    public boolean isInteractDisabled() {
        return interactDisabled;
    }

    public Button ifThen(Predicate<Button> condition, Function<Button, Button> action) {
        if (condition.test(this)) {
            return action.apply(this);
        }
        return this;
    }

    public Button cloneButton() {
        ItemStack clonedItem = item.clone();
        Button clonedButton = new Button(clonedItem);
        clonedButton
                .disableInteract(interactDisabled)
                .withListener(listener)
                .setStatic(staticated)
                .setPosition(position);
        return clonedButton;
    }

    public Button applyMeta(Function<ItemMeta, ItemMeta> metaFunction) {
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(metaFunction.apply(meta));
        return this;
    }

    public Button addLoreLine(String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public Button removeLoreLine(int index) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (index >= 0 && index < lore.size()) {
                lore.remove(index);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        return this;
    }

    public Button clearLore() {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);
        return this;
    }

    public Button setHeadTexture(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Texture value cannot be null or empty.");
        }

        ItemMeta meta = this.item.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("ItemMeta cannot be null.");
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
            this.item.setItemMeta(meta);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Button setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public Button withListener(ButtonListener listener) {
        this.listener = listener;
        return this;
    }

    public void executeListener(InventoryClickEvent event) {
        if (hasListener()) {
            listener.execute(event);
        }
    }

    public ButtonListener getListener() {
        return listener;
    }

    public boolean hasListener() {
        return listener != null;
    }


    public String getDisplayFormat() {
        return displayFormat;
    }
}