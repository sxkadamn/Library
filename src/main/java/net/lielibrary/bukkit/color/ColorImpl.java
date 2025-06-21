package net.lielibrary.bukkit.color;


import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorImpl implements BukkitColor {


   @Override
    public String hexToMinecraftColor(String text) {
        if (text == null) return "";

        Matcher gradientMatcher = Pattern.compile("\\{#([A-Fa-f0-9]{6})>#([A-Fa-f0-9]{6})}([^{}]*)").matcher(text);
        StringBuilder gradientConverted = new StringBuilder();

        while (gradientMatcher.find()) {
            String startHex = gradientMatcher.group(1);
            String endHex = gradientMatcher.group(2);
            String content = gradientMatcher.group(3);

            String gradientText = applyGradient(content, startHex, endHex);
            gradientMatcher.appendReplacement(gradientConverted, gradientText);
        }
        gradientMatcher.appendTail(gradientConverted);

        Matcher hexMatcher = Pattern.compile("#[a-fA-F0-9]{6}").matcher(gradientConverted.toString());
        StringBuilder hexConverted = new StringBuilder();

        while (hexMatcher.find()) {
            String hex = hexMatcher.group();
            String replacement = "§x" + hex.substring(1).replaceAll("(.)", "§$1");
            hexMatcher.appendReplacement(hexConverted, replacement);
        }
        hexMatcher.appendTail(hexConverted);

        return hexConverted.toString().replaceAll("&([0-9a-fA-Fk-oK-OrR])", "§$1");
    }

    private String applyGradient(String text, String startHex, String endHex) {
        int length = text.length();
        if (length == 0) return "";

        Color startColor = Color.decode("#" + startHex);
        Color endColor = Color.decode("#" + endHex);

        StringBuilder gradientText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);
            int red = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * ratio);
            int green = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * ratio);
            int blue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * ratio);

            String hexColor = String.format("#%02X%02X%02X", red, green, blue);
            String minecraftColor = "§x" + hexColor.substring(1).replaceAll("(.)", "§$1");

            gradientText.append(minecraftColor).append(text.charAt(i));
        }
        return gradientText.toString();
    }
}

//    public String minecraftColorToHex(String text) {
//        Map<Character, String> colorMap = new HashMap<>();
//        colorMap.put('0', "#000000"); // Черный
//        colorMap.put('1', "#0000AA"); // Темно-синий
//        colorMap.put('2', "#00AA00"); // Темно-зеленый
//        colorMap.put('3', "#00AAAA"); // Бирюзовый
//        colorMap.put('4', "#AA0000"); // Темно-красный
//        colorMap.put('5', "#AA00AA"); // Фиолетовый
//        colorMap.put('6', "#FFAA00"); // Оранжевый
//        colorMap.put('7', "#AAAAAA"); // Серый
//        colorMap.put('8', "#555555"); // Темно-серый
//        colorMap.put('9', "#5555FF"); // Синий
//        colorMap.put('a', "#55FF55"); // Зеленый
//        colorMap.put('b', "#55FFFF"); // Голубой
//        colorMap.put('c', "#FF5555"); // Красный
//        colorMap.put('d', "#FF55FF"); // Розовый
//        colorMap.put('e', "#FFFF55"); // Желтый
//        colorMap.put('f', "#FFFFFF"); // Белый
//
//        for (Map.Entry<Character, String> entry : colorMap.entrySet()) {
//            text = text.replace("&" + entry.getKey(), entry.getValue());
//        }
//
//        return text;
//    }
