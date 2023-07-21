package org.timothek.HungerGames.game;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.timothek.HungerGames.Main;
import java.util.*;

public class LobbyController {

    int players = 0;
    int minPlayers = 2;
    int maxPlayers = 10;
    BukkitTask timerToStart;
    int maxSecondsToStart = 60;
    int secondsToStart = maxSecondsToStart;
    int secondsToStartMaxPlayers = 5;

    public Main plugin;

    public LobbyController(Main plugin){
        this.plugin = plugin;
    }

    public void playerJoin(Player player){
        players++;
        if(players == minPlayers){
            startTimer();
        }
    }

    public void resetController(){
        secondsToStart = maxSecondsToStart;
    }

    public void playerLeave(){
        players--;
        if(players < minPlayers){
            timerToStart.cancel();
            Collection<? extends Player> players =  Bukkit.getServer().getOnlinePlayers();
            for(Player player : players){
                player.sendMessage("Отсчет до начала игры приостановлен");
            }
        }

    }

    public void startTimer(){
        timerToStart = Bukkit.getServer().getScheduler().runTaskTimer(plugin, this::waitForPlayers, 0, 20L);
    }

    public void stopTimer(){
        timerToStart.cancel();
    }

    public void createGameWorldInAnotherThread(){
        WorldCreator wc = new WorldCreator("playing");
        wc.type(WorldType.NORMAL);
        Main.lobbyWorld = Bukkit.getWorld("world");
        Main.gameWorld = wc.createWorld();
    }

    public void waitForPlayers() {
        if(Main.gameInProgress) stopTimer();
        Collection<? extends Player> players =  Bukkit.getServer().getOnlinePlayers();
        for(Player player : players){
            player.sendMessage("До начала игры " + secondsToStart + " секунд");
        }
        secondsToStart -= 1;
        if(secondsToStart <= 5){
            Main.lobbyWorld.playSound(Main.lobbyWorld.getSpawnLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 10);
        }
        if(secondsToStart == 0){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "startgame");
        }
        if(this.players == maxPlayers){
            secondsToStart = secondsToStartMaxPlayers;
        }
    }
}
