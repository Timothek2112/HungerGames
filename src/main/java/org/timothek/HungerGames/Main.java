package org.timothek.HungerGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.timothek.HungerGames.commands.SpawnLootCMD;
import org.timothek.HungerGames.commands.StartCMD;
import org.timothek.HungerGames.commands.StopCMD;
import org.timothek.HungerGames.events.Events;
import org.timothek.HungerGames.game.GameController;
import org.timothek.HungerGames.game.LobbyController;

import java.util.ArrayList;

public class Main extends JavaPlugin {
    public static World gameWorld;
    public static World lobbyWorld;
    public static boolean gameInProgress = false;
    public static LobbyController controller;
    public static GameController gameController;
    public static boolean damageOff = true;
    public static Main plugin;


    @Override
    public void onEnable(){
        plugin = this;
        this.getLogger().info( ChatColor.GREEN + "Плагин включен - " + this.getName());
        getCommand("startgame").setExecutor(new StartCMD());
        getCommand("stopgame").setExecutor(new StopCMD());
        getCommand("spawnloot").setExecutor(new SpawnLootCMD());
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        controller = new LobbyController(this);
        gameController = new GameController(this, 0, new ArrayList<>());
        controller.createGameWorldInAnotherThread();
    }

    @Override
    public void onLoad(){

    }

    @Override
    public void onDisable(){
        this.getLogger().info(ChatColor.RED + "Плагин выключен - " + this.getName());
    }
}
