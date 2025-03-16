package net.lielibrary.bukkit.requirements;

import net.lielibrary.bukkit.requirements.commands.ActionBarCommand;
import net.lielibrary.bukkit.requirements.commands.BroadcastCommand;
import net.lielibrary.bukkit.requirements.commands.ConsoleCommand;
import net.lielibrary.bukkit.requirements.commands.MessageCommand;

import java.util.HashMap;
import java.util.Map;

public class RequirementFactory {

    private static final Map<String, Class<? extends RequireHandler>> commandMap = new HashMap<>();

    static {
        register("[MESSAGE]", MessageCommand.class);
        register("[CONSOLE]", ConsoleCommand.class);
        register("[ACTION_BAR]", ActionBarCommand.class);
        register("[BROADCAST]", BroadcastCommand.class);
    }

    public static void register(String prefix, Class<? extends RequireHandler> handlerClass) {
        commandMap.put(prefix, handlerClass);
    }

    public static RequireHandler create(String commandString) {
        for (Map.Entry<String, Class<? extends RequireHandler>> entry : commandMap.entrySet()) {
            if (commandString.startsWith(entry.getKey())) {
                try {
                    String content = commandString.substring(entry.getKey().length()).trim();
                    return entry.getValue().getConstructor(String.class).newInstance(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
