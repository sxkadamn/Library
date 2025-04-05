package net.lielibrary.bukkit.command;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class RegisterCommand extends Command implements PluginIdentifiableCommand {
    private final Plugin plugin;
    private final CommandExecutor owner;
    private final TabCompleter tabCompleter;
    private static CommandMap commandMap;

    public RegisterCommand(List<String> aliases, String desc, String usage, CommandExecutor owner, Plugin plugin) {
        super(aliases.get(0), desc, usage, aliases);
        this.owner = owner;
        this.plugin = plugin;
        this.tabCompleter = (owner instanceof TabCompleter) ? (TabCompleter) owner : null;
    }

    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return this.owner.onCommand(sender, this, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (tabCompleter != null) {
            return Objects.requireNonNull(tabCompleter.onTabComplete(sender, this, alias, args));
        }
        return super.tabComplete(sender, alias, args);
    }

    public static void reg(Plugin plugin, CommandExecutor executor, List<String> aliases, String desc, String usage) {
        try {
            RegisterCommand reg = new RegisterCommand(aliases, desc, usage, executor, plugin);
            if (commandMap == null) {
                String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
                Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
                Object craftServerObject = craftServerClass.cast(plugin.getServer());
                Field commandMapField = craftServerClass.getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(craftServerObject);
            }
            commandMap.register(plugin.getDescription().getName(), reg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}