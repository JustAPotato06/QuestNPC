package dev.potato.questnpc.commands;

import dev.potato.questnpc.QuestNPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        player.sendMessage(ChatColor.GREEN + "[QUEST NPC] Your current balance is: " + ChatColor.GOLD + "$" + QuestNPC.getEconomy().getBalance(player));

        return true;
    }
}