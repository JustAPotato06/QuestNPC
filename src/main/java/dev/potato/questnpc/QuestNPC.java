package dev.potato.questnpc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import dev.potato.questnpc.commands.QuestNPCCommand;
import dev.potato.questnpc.listeners.QuestListeners;
import dev.potato.questnpc.menus.QuestMenu;
import dev.potato.questnpc.utilities.NPCUtilities;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;


/*
KNOWN ISSUES:

- Quest NPC only shows for the player that runs the command
- There can only be 1 quest NPC
- Currency earned does nothing
- Item Quests aren't a thing

NOTE: The chance that these will be fixed is very low. This plugin wasn't really meant for proper use, just for learning.
*/

public final class QuestNPC extends JavaPlugin {
    private static QuestNPC plugin;
    private final NPCUtilities npcManager = NPCUtilities.getNpcManager();

    public static QuestNPC getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Initialization
        plugin = this;

        // Configuration
        registerConfiguration();

        // Listeners
        registerListeners();

        // Commands
        registerCommands();

        // Simp API - Menus
        registerMenus();

        // Packet Listeners
        registerPacketListeners();
    }

    private void registerConfiguration() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new QuestListeners(), this);
    }

    private void registerCommands() {
        getCommand("questnpc").setExecutor(new QuestNPCCommand());
    }

    private void registerMenus() {
        MenuManager.setup(getServer(), this);
    }

    private void registerPacketListeners() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        // NPC Interact Listener
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                // Packet Data
                int entityID;
                EnumWrappers.Hand hand;
                EnumWrappers.EntityUseAction action;
                try {
                    entityID = packet.getIntegers().read(0);
                    hand = packet.getEnumEntityUseActions().read(0).getHand();
                    action = packet.getEnumEntityUseActions().read(0).getAction();
                } catch (IllegalArgumentException e) {
                    return;
                }

                // Checks
                if (entityID != npcManager.getQuestNPCID()) return;
                if (hand != EnumWrappers.Hand.MAIN_HAND || action != EnumWrappers.EntityUseAction.INTERACT) return;

                getServer().getScheduler().runTask(plugin, () -> {
                    // Open Quest Menu
                    try {
                        MenuManager.openMenu(QuestMenu.class, event.getPlayer());
                    } catch (MenuManagerException | MenuManagerNotSetupException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }
}