package dev.potato.questnpc.listeners;

import dev.potato.questnpc.models.KillQuest;
import dev.potato.questnpc.models.Quest;
import dev.potato.questnpc.utilities.QuestUtilities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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
            player.sendMessage(ChatColor.YELLOW + "[QUEST NPC] You need to kill " + (killQuest.getAmountToKill() - killQuest.getProgress()) + " more " + killQuest.getEntityType().name().toLowerCase() + "s.");
        }

    }
}