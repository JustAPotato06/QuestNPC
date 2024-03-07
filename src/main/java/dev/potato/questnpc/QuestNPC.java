package dev.potato.questnpc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.GameProfile;
import dev.potato.questnpc.commands.BalanceCommand;
import dev.potato.questnpc.commands.QuestNPCCommand;
import dev.potato.questnpc.configuration.NPCConfig;
import dev.potato.questnpc.listeners.NPCListeners;
import dev.potato.questnpc.listeners.QuestListeners;
import dev.potato.questnpc.menus.QuestMenu;
import dev.potato.questnpc.utilities.NPCUtilities;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


/*
KNOWN ISSUES:

- Item Quests aren't a thing
- NPC facing mechanics don't work yet

*/

public final class QuestNPC extends JavaPlugin {
    private static QuestNPC plugin;
    private final NPCUtilities npcManager = NPCUtilities.getNpcManager();
    private static Economy economy = null;

    public static QuestNPC getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return economy;
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

        // Vault
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Quest NPC was disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Packet Listeners
        registerPacketListeners();
    }

    @Override
    public void onDisable() {
        saveNPCs();
    }

    private void saveNPCs() {
        FileConfiguration npcConfig = NPCConfig.get();
        List<ServerPlayer> questNPCs = npcManager.getQuestNPCs();
        for (int i = 0; i < questNPCs.size(); i++) {
            ServerPlayer npc = questNPCs.get(i);

            ConfigurationSection section = npcConfig.createSection("npc-" + i);
            GameProfile gameProfile = npc.getGameProfile();
            section.addDefault("uuid", gameProfile.getId().toString());
            section.addDefault("display-name", gameProfile.getName());

            ConfigurationSection locationSection = section.createSection("location");
            Location npcLocation = npc.getBukkitEntity().getLocation();
            locationSection.addDefault("x", npcLocation.getX());
            locationSection.addDefault("y", npcLocation.getY());
            locationSection.addDefault("z", npcLocation.getZ());
            locationSection.addDefault("yaw", npcLocation.getYaw());
            locationSection.addDefault("pitch", npcLocation.getPitch());
        }
        NPCConfig.save();
    }

    private void registerConfiguration() {
        // Config.yml
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // NPCs.yml
        NPCConfig.setup();
        NPCConfig.get().options().copyDefaults(true);
        NPCConfig.get().set("is-loaded", false);
        NPCConfig.save();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new QuestListeners(), this);
        getServer().getPluginManager().registerEvents(new NPCListeners(), this);
    }

    private void registerCommands() {
        getCommand("questnpc").setExecutor(new QuestNPCCommand());
        if (getConfig().getBoolean("enable-balance-command")) {
            getCommand("balance").setExecutor(new BalanceCommand());
        }
    }

    private void registerMenus() {
        MenuManager.setup(getServer(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
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
                boolean isNPC = false;
                for (ServerPlayer npc : npcManager.getQuestNPCs()) {
                    if (entityID == npcManager.getQuestNPCID(npc.displayName)) isNPC = true;
                }
                if (!isNPC) return;
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