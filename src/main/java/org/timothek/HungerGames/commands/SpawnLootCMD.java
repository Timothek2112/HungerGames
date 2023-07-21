package org.timothek.HungerGames.commands;

import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.timothek.HungerGames.Main;

public class SpawnLootCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Main.gameController.spawnLoot(0, 0, 0);
        return true;
    }
}
