package dev.potato.questnpc.utilities;

import dev.potato.questnpc.QuestNPC;
import dev.potato.questnpc.models.KillQuest;
import dev.potato.questnpc.models.Quest;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestUtilities {
    private static final QuestUtilities questManager = new QuestUtilities();
    private final HashMap<Player, Quest> activeQuests = new HashMap<>();

    public static QuestUtilities getQuestManager() {
        return questManager;
    }

    public void giveQuest(Player player, Quest quest) {
        quest.setWhenStarted(System.currentTimeMillis());
        activeQuests.put(player, quest);
    }

    public void completeQuest(Player player) {
        Quest quest = activeQuests.get(player);
        long timeTaken = System.currentTimeMillis() - quest.getWhenStarted();
        String timeTakenString = String.format("%02d:%02d", timeTaken / 60000, (timeTaken % 60000) / 1000);
        QuestNPC.getEconomy().depositPlayer(player, quest.getRewardAmount());
        player.sendMessage(ColorTranslator.translateColorCodes("&a[QUEST NPC] You completed the quest &7" + quest.getName() + "&a in " + timeTakenString + " seconds!"));
        player.sendMessage(ColorTranslator.translateColorCodes("&a[QUEST NPC] You received " + quest.getRewardAmount() + " dollars."));
        activeQuests.remove(player);
    }

    // Returns null if the player has no quests
    public Quest getActiveQuest(Player player) {
        return activeQuests.get(player);
    }

    public List<Quest> getAvailableQuests() {
        List<Quest> availableQuests = new ArrayList<>();
        FileConfiguration config = QuestNPC.getPlugin().getConfig();

        config.getConfigurationSection("quests.kill").getKeys(false)
                .forEach(questName -> {
                    String name = config.getString("quests.kill." + questName + ".name");
                    String description = config.getString("quests.kill." + questName + ".description");
                    double reward = config.getDouble("quests.kill." + questName + ".reward");
                    String entityTypeString = config.getString("quests.kill." + questName + ".target.type");
                    EntityType entityType = EntityType.valueOf(entityTypeString);
                    int count = config.getInt("quests.kill." + questName + ".target.count");

                    Quest quest = new KillQuest(name, description, reward, entityType, count);
                    availableQuests.add(quest);
                });

        return availableQuests;
    }
}