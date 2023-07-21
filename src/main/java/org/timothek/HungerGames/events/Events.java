package org.timothek.HungerGames.events;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.timothek.HungerGames.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();
        Main.controller.playerJoin(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        if(!Main.gameInProgress) return;
        Main.gameController.playerDeath(((Player) event.getEntity()).getPlayer(), event.getEntity().getKiller());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Main.controller.playerLeave();
        if(!Main.gameInProgress) return;
        Main.gameController.playerLeave(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamageEvent(final EntityDamageEvent event){
        if(!Main.damageOff) return;
        if(!(event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
    }


}
