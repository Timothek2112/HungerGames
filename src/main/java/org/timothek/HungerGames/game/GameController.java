package org.timothek.HungerGames.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.timothek.HungerGames.Main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GameController {
    BukkitTask timerToSpawnLoot;
    BukkitTask timerToOnDamage;
    int secondsToSpawnLoot = 300;
    int secondsToOnDamage = 30;
    int announceDamageEvery = 10;
    int announceLootEvery = 30;
    int playersAlive = 0;
    ArrayList<Player> playersInGame;
    Main plugin;

    public GameController(Main plugin, int playersAlive, ArrayList<Player> playersInGame){
        this.plugin = plugin;
        this.playersAlive = playersAlive;
        this.playersInGame = playersInGame;
    }

    public void startGame(){
        startTimers();
    }

    public void stopGame(){
        stopTimers();
        Main.gameInProgress = false;
    }

    public void playerLeave(Player player){
        playersAlive--;
        playersInGame.remove(player);
        checkForWinner();
    }

    public void playerDeath(Player player, Player killer){
        plugin.getServer().broadcastMessage("&4Игрок &f " + player.getName() + "&4 был убит игроком &f" + killer.getName());
        playersInGame.remove(player);
        playersAlive--;
        checkForWinner();
    }

    public void checkForWinner(){
        if(playersAlive == 1){
            stopGame();
            plugin.getServer().broadcastMessage("&6&lПобедил игрок &f" + playersInGame.get(0).getName());
            try {
                plugin.getServer().getWorld("playing").spawnEntity(playersInGame.get(0).getLocation(), EntityType.FIREWORK);
                Thread.sleep(20L);
                plugin.getServer().getWorld("playing").spawnEntity(playersInGame.get(0).getLocation(), EntityType.FIREWORK);
                Thread.sleep(20L);
                plugin.getServer().getWorld("playing").spawnEntity(playersInGame.get(0).getLocation(), EntityType.FIREWORK);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {teleportAllPlayersToMainLobby();}, 5 * 20L);
        }
    }

    public void startTimers(){
        waitForSpawnFirstLoot();
        waitForOnDamage();
    }

    public void stopTimers(){
        timerToOnDamage.cancel();
        timerToSpawnLoot.cancel();
    }

    public void waitForOnDamage(){
        timerToOnDamage = Bukkit.getServer().getScheduler().runTaskTimer(plugin, this::damageTimerTick, 0, 20L);
    }

    public void waitForSpawnFirstLoot(){
        timerToSpawnLoot = Bukkit.getServer().getScheduler().runTaskTimer(plugin, this::spawnLootTick, 0, 20L);
    }

    private void damageTimerTick(){
        if(secondsToOnDamage % announceDamageEvery == 0 || secondsToOnDamage <= 5){
            Bukkit.getServer().broadcastMessage("&eДо включения урона осталось: &f" + secondsToOnDamage + "&e секунд!");
        }
        if(secondsToOnDamage == 0){
            Bukkit.getServer().broadcastMessage("&c&lТеперь игроки могут наносить урон друг другу!");
            Main.damageOff = false;
            timerToOnDamage.cancel();
        }
        secondsToOnDamage--;
    }

    private void spawnLootTick(){
        if(secondsToSpawnLoot % announceLootEvery == 0 || secondsToSpawnLoot <= 5){
            Bukkit.getServer().broadcastMessage("&eДо появления платформы с лутом осталось &f" + secondsToSpawnLoot + "&e секунд!");
        }
        if(secondsToSpawnLoot == 0){
            Bukkit.getServer().broadcastMessage("&2&lПлатформа с лутом появилась!");
            int[] point = generatePointToSpawnLoot();
            spawnLoot(point[0], point[1], point[2]);
            timerToSpawnLoot.cancel();
        }
        secondsToSpawnLoot--;
    }

    public int[] generatePointToSpawnLoot(){
        int x = ThreadLocalRandom.current().nextInt(-1000, 1000);
        int z = ThreadLocalRandom.current().nextInt(-1000, 1000);
        int y = Main.gameWorld.getHighestBlockYAt(x, z);
        return new int[] {x,y,z};
    }

    public void spawnLoot(int x, int y, int z){
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/schem load lootPlane");
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/paste");
        File lootPlaneSchem = new File("./FAWEschemas/lootPlane.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(lootPlaneSchem);
        Clipboard clipboard;
        try(ClipboardReader reader = format.getReader(new FileInputStream(lootPlaneSchem))) {
            clipboard = reader.read();

        } catch (Exception ex){ throw new RuntimeException(ex.getMessage()); }

        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(Main.gameWorld), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public void teleportAllPlayersToMainLobby(){
            for(Player curPlayer : Main.lobbyWorld.getPlayers()){
                curPlayer.performCommand("server lobby");
            }
    }
}
