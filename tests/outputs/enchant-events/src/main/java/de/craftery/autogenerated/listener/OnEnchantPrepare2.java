package de.craftery.autogenerated.listener;

import de.craftery.autogenerated.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class OnEnchantPrepare2 implements Listener {
    public OnEnchantPrepare2() {
        Main.registerEvent(this);
    }
    
    @EventHandler
    public void onEnchantPrepare(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        if (player.isOp()) {
            return;
        }
        else {
            event.setCancelled(true);
        }
    }
    
}

