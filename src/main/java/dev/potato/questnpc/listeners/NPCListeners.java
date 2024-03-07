package dev.potato.questnpc.listeners;

import dev.potato.questnpc.configuration.NPCConfig;
import dev.potato.questnpc.utilities.NPCUtilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NPCListeners implements Listener {
    private final NPCUtilities npcManager = NPCUtilities.getNpcManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Load NPCs
        FileConfiguration npcConfig = NPCConfig.get();
        boolean isLoaded = npcConfig.getBoolean("is-loaded");
        if (!isLoaded) {
            npcConfig.getKeys(false).forEach(sectionName -> {
                if (!sectionName.equalsIgnoreCase("is-loaded")) {
                    String uuid = npcConfig.getString(sectionName + ".uuid");
                    String displayName = npcConfig.getString(sectionName + ".display-name");
                    double x = npcConfig.getDouble(sectionName + ".location.x");
                    double y = npcConfig.getDouble(sectionName + ".location.y");
                    double z = npcConfig.getDouble(sectionName + ".location.z");
                    float yaw = (float) npcConfig.getDouble(sectionName + ".location.yaw");
                    float pitch = (float) npcConfig.getDouble(sectionName + ".location.pitch");
                    npcManager.loadQuestNPC(player, uuid, displayName, x, y, z, yaw, pitch);
                }
            });
            npcConfig.set("is-loaded", true);
            NPCConfig.save();
        }

        // Show NPCs
        npcManager.showQuestNPCs(player);
    }
}