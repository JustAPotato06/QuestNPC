package dev.potato.questnpc.menus;

import dev.potato.questnpc.models.ItemQuest;
import dev.potato.questnpc.models.KillQuest;
import dev.potato.questnpc.models.Quest;
import dev.potato.questnpc.utilities.QuestUtilities;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class QuestMenu extends Menu {
    private final QuestUtilities questManager = QuestUtilities.getQuestManager();

    public QuestMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Quest Menu";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem.getType() == Material.DIAMOND_SWORD) {
            String questName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());
            for (Quest quest : questManager.getAvailableQuests()) {
                if (!quest.getName().equalsIgnoreCase(questName)) continue;
                Quest activeQuest = questManager.getActiveQuest(p);
                if (activeQuest != null) {
                    if (activeQuest.getName().equalsIgnoreCase(questName)) {
                        p.sendMessage(ChatColor.RED + "[QUEST NPC] You already have this quest going!");
                    } else {
                        p.sendMessage(ChatColor.RED + "[QUEST NPC] You already have an active quest going!");
                    }
                } else {
                    questManager.giveQuest(p, quest);
                    p.sendMessage(ColorTranslator.translateColorCodes("&aYou have been given the quest &e" + quest.getName() + "&a!"));
                    p.sendMessage(ColorTranslator.translateColorCodes("&aTo complete this quest, you must &e" + quest.getDescription() + "&a!"));
                }
                p.closeInventory();
                break;
            }
        } else if (currentItem.getType() == Material.DIAMOND_PICKAXE) {
            String questName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());
            for (Quest quest : questManager.getAvailableQuests()) {
                if (!quest.getName().equalsIgnoreCase(questName)) continue;
                Quest activeQuest = questManager.getActiveQuest(p);
                if (activeQuest != null) {
                    if (activeQuest.getName().equalsIgnoreCase(questName)) {
                        p.sendMessage(ChatColor.RED + "[QUEST NPC] You already have this quest going!");
                    } else {
                        p.sendMessage(ChatColor.RED + "[QUEST NPC] You already have an active quest going!");
                    }
                } else {
                    questManager.giveQuest(p, quest);
                    p.sendMessage(ColorTranslator.translateColorCodes("&aYou have been given the quest &e" + quest.getName() + "&a!"));
                    p.sendMessage(ColorTranslator.translateColorCodes("&aTo complete this quest, you must &e" + quest.getDescription() + "&a!"));
                }
                p.closeInventory();
                break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        questManager.getAvailableQuests().forEach(quest -> {
            ItemStack item;
            if (quest instanceof KillQuest killQuest) {
                Quest playersQuest = questManager.getActiveQuest(p);
                boolean isOnQuest = playersQuest != null && playersQuest.getName().equalsIgnoreCase(quest.getName());
                item = makeItem(Material.DIAMOND_SWORD,
                        ColorTranslator.translateColorCodes("&6&l" + killQuest.getName()),
                        ColorTranslator.translateColorCodes("&5" + killQuest.getDescription()),
                        " ",
                        "&7Reward: &a$" + killQuest.getRewardAmount(),
                        " ",
                        isOnQuest ? "&cYou are on this quest!" : "&aClick to accept!");
                inventory.addItem(item);
            } else if (quest instanceof ItemQuest itemQuest) {
                Quest playersQuest = questManager.getActiveQuest(p);
                boolean isOnQuest = playersQuest != null && playersQuest.getName().equalsIgnoreCase(quest.getName());
                item = makeItem(Material.DIAMOND_PICKAXE,
                        ColorTranslator.translateColorCodes("&6&l" + itemQuest.getName()),
                        ColorTranslator.translateColorCodes("&5" + itemQuest.getDescription()),
                        " ",
                        "&7Reward: &a$" + itemQuest.getRewardAmount(),
                        " ",
                        isOnQuest ? "&cYou are on this quest!" : "&aClick to accept!");
                inventory.addItem(item);
            }
        });
        setFillerGlass();
    }
}