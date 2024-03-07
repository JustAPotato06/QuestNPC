package dev.potato.questnpc.commands;

import dev.potato.questnpc.utilities.NPCUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuestNPCCommand implements TabExecutor {
    private final NPCUtilities npcManager = NPCUtilities.getNpcManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "[QUEST NPC] Invalid number of arguments provided!");
            p.sendMessage(ChatColor.RED + "[QUEST NPC] Example usage: /questnpc [NPC Name]");
            return true;
        }

        // Spawn NPC
        npcManager.spawnQuestNPC(p, args[0]);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("[NPC Name]");
        }
        return completions;
    }
}