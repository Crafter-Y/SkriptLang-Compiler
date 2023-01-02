package de.craftery.autogenerated.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StaffannounceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder argumentBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            argumentBuilder.append(args[i]);
            if (i != args.length) argumentBuilder.append(" ");
        }
        String argument1 = argumentBuilder.toString();
        
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§c§l[ANNOUNCEMENT] §f" + argument1 + ""));
        Bukkit.broadcast(Component.text(""));
        return true;
    }
    
}

