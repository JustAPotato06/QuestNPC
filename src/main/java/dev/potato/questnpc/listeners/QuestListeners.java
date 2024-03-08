package dev.potato.questnpc.listeners;

import dev.potato.questnpc.models.ItemQuest;
import dev.potato.questnpc.models.KillQuest;
import dev.potato.questnpc.models.Quest;
import dev.potato.questnpc.utilities.QuestUtilities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class QuestListeners implements Listener {
    private final QuestUtilities questManager = QuestUtilities.getQuestManager();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player player = e.getEntity().getKiller();
        Quest quest = questManager.getActiveQuest(player);

        if (!(quest instanceof KillQuest killQuest)) return;
        if (killQuest.getEntityType() != e.getEntityType()) return;

        killQuest.setProgress(killQuest.getProgress() + 1);

        if (killQuest.getProgress() == killQuest.getAmountToKill()) {
            questManager.completeQuest(player);
        } else {
            player.sendMessage(ChatColor.GREEN + "[QUEST NPC] You have killed " + ChatColor.GOLD + killQuest.getProgress() + ChatColor.GREEN + " " + killQuest.getEntityType().name().toLowerCase() + "s.");
            player.sendMessage(ChatColor.YELLOW + "[QUEST NPC] You need to kill " + ChatColor.GOLD + (killQuest.getAmountToKill() - killQuest.getProgress()) + ChatColor.GREEN + " more " + killQuest.getEntityType().name().toLowerCase() + "s.");
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player player) {
            ItemStack item = e.getItem().getItemStack();
            Quest quest = questManager.getActiveQuest(player);

            if (!(quest instanceof ItemQuest itemQuest)) return;
            if (itemQuest.getMaterial() != item.getType()) return;

            itemQuest.setProgress(itemQuest.getProgress() + item.getAmount());

            if (itemQuest.getProgress() >= itemQuest.getAmountToCollect()) {
                questManager.completeQuest(player);
            } else {
                player.sendMessage(ChatColor.GREEN + "[QUEST NPC] You have collected " + ChatColor.GOLD + itemQuest.getProgress() + ChatColor.GREEN + " " + itemQuest.getMaterial().name().toLowerCase() + "s.");
                player.sendMessage(ChatColor.YELLOW + "[QUEST NPC] You need to collect " + ChatColor.GOLD + (itemQuest.getAmountToCollect() - itemQuest.getProgress()) + ChatColor.GREEN + " more " + itemQuest.getMaterial().name().toLowerCase() + "s.");
            }
        }
    }
}