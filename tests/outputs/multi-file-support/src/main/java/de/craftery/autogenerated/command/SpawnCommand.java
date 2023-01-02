package de.craftery.autogenerated.command;

import de.craftery.autogenerated.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is only for players!");
            return true;
        }
        Player player = (Player) sender;
        
        if (args.length > 0) {
            sender.sendMessage("Correct usage: /spawn");
            return true;
        }
        
        player.teleport((Location) Main.getVariable("spawn"));
        player.sendMessage(Component.text("§cTeleported to spawn."));
        return true;
    }
    
}
