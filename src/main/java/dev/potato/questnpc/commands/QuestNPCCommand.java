package dev.potato.questnpc.commands;

import dev.potato.questnpc.utilities.NPCUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestNPCCommand implements CommandExecutor {
    private final NPCUtilities npcManager = NPCUtilities.getNpcManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return true;

        // Spawn NPC
        npcManager.spawnQuestNPC(p);

        return true;
    }
}