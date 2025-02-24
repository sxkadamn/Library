package net.lielibrary.bukkit.color;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorImpl implements BukkitColor{

    public String hexToMinecraftColor(String text) {
        Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(text);

        while (matcher.find()) {
            String hex = matcher.group();
            String mcColor = getNearestMinecraftColor(hex);
            text = text.replace(hex, mcColor);
        }

        return text.replace("&", "§"); // Меняем на Minecraft-коды
    }

    private String getNearestMinecraftColor(String hex) {
        switch (hex.toUpperCase()) {
            case "#000000": return "&0";
            case "#0000AA": return "&1";
            case "#00AA00": return "&2";
            case "#00AAAA": return "&3";
            case "#AA0000": return "&4";
            case "#AA00AA": return "&5";
            case "#FFAA00": return "&6";
            case "#AAAAAA": return "&7";
            case "#555555": return "&8";
            case "#5555FF": return "&9";
            case "#55FF55": return "&a";
            case "#55FFFF": return "&b";
            case "#FF5555": return "&c";
            case "#FF55FF": return "&d";
            case "#FFFF55": return "&e";
            case "#FFFFFF": return "&f";
            default: return "&7";
        }
    }
    public String minecraftColorToHex(String text) {
        Map<Character, String> colorMap = new HashMap<>();
        colorMap.put('0', "#000000"); // Черный
        colorMap.put('1', "#0000AA"); // Темно-синий
        colorMap.put('2', "#00AA00"); // Темно-зеленый
        colorMap.put('3', "#00AAAA"); // Бирюзовый
        colorMap.put('4', "#AA0000"); // Темно-красный
        colorMap.put('5', "#AA00AA"); // Фиолетовый
        colorMap.put('6', "#FFAA00"); // Оранжевый
        colorMap.put('7', "#AAAAAA"); // Серый
        colorMap.put('8', "#555555"); // Темно-серый
        colorMap.put('9', "#5555FF"); // Синий
        colorMap.put('a', "#55FF55"); // Зеленый
        colorMap.put('b', "#55FFFF"); // Голубой
        colorMap.put('c', "#FF5555"); // Красный
        colorMap.put('d', "#FF55FF"); // Розовый
        colorMap.put('e', "#FFFF55"); // Желтый
        colorMap.put('f', "#FFFFFF"); // Белый

        for (Map.Entry<Character, String> entry : colorMap.entrySet()) {
            text = text.replace("&" + entry.getKey(), entry.getValue());
        }

        return text;
    }
}
