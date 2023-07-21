package org.timothek.HungerGames.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.timothek.HungerGames.Main;

public class StopCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!Main.gameInProgress) return false;
        teleportAllPlayersToLobbyWorld();
        Bukkit.getServer().unloadWorld(Main.gameWorld, true);
        Main.gameWorld = null;
        Main.gameInProgress = false;
        return true;
    }

    private void teleportAllPlayersToLobbyWorld(){
        for(Player player : Main.gameWorld.getPlayers()){
            player.teleport(new Location(Main.lobbyWorld, Main.lobbyWorld.getSpawnLocation().getX(), Main.lobbyWorld.getSpawnLocation().getY(), Main.lobbyWorld.getSpawnLocation().getZ()));
        }
        Main.controller.resetController();
        Main.gameController.stopGame();
    }
}
