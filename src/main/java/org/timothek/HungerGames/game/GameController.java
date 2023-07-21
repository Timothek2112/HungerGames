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
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.timothek.HungerGames.Main;

import java.io.File;
import java.io.FileInputStream;

public class GameController {
    BukkitTask timerToSpawnLoot;
    BukkitTask timerToOnDamage;
    int secondsToSpawnLoot = 300;
    int secondsToOnDamage = 30;
    int announceDamageEvery = 10;
    int announceLootEvery = 30;
    Main plugin;

    public GameController(Main plugin){
        this.plugin = plugin;
    }

    public void startGame(){
        startTimers();
    }

    public void stopGame(){
        stopTimers();
        Main.gameInProgress = false;
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
            Bukkit.getServer().broadcastMessage("До включения урона осталось: " + secondsToOnDamage + " секунд!");
        }
        if(secondsToOnDamage == 0){
            Bukkit.getServer().broadcastMessage("Теперь игроки могут наносить урон друг другу!");
            Main.damageOff = false;
            timerToOnDamage.cancel();
        }
        secondsToOnDamage--;
    }

    private void spawnLootTick(){
        if(secondsToSpawnLoot % announceLootEvery == 0 || secondsToSpawnLoot <= 5){
            Bukkit.getServer().broadcastMessage("До появления платформы с лутом осталось " + secondsToSpawnLoot + " секунд!");
        }
        if(secondsToSpawnLoot == 0){
            Bukkit.getServer().broadcastMessage("Платформа с лутом появилась!");
            spawnLoot(0, 0, 0);
            timerToSpawnLoot.cancel();
        }
        secondsToSpawnLoot--;
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
}
