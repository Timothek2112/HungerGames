package org.timothek.HungerGames.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.timothek.HungerGames.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StartCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(Main.gameInProgress) return false;
        if(Main.gameWorld == null) return false;
        Main.gameInProgress = true;
        teleportAllPlayersToGameWorld();
        Main.gameController.startGame();
        return true;
    }

    private void teleportAllPlayersToGameWorld(){
        for(Player curPlayer : Main.lobbyWorld.getPlayers()){
            curPlayer.teleport(new Location(Main.gameWorld, Main.gameWorld.getSpawnLocation().getX(), Main.gameWorld.getSpawnLocation().getY(), Main.gameWorld.getSpawnLocation().getZ()));
        }
    }



}
