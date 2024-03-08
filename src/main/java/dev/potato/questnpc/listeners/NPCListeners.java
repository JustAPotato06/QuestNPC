package dev.potato.questnpc.listeners;

import dev.potato.questnpc.configuration.NPCConfig;
import dev.potato.questnpc.utilities.NPCUtilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

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
                    double directionX = npcConfig.getDouble(sectionName + ".direction.x");
                    double directionY = npcConfig.getDouble(sectionName + ".direction.y");
                    double directionZ = npcConfig.getDouble(sectionName + ".direction.z");
                    npcManager.loadQuestNPC(player, uuid, displayName, x, y, z, yaw, pitch, new Vector(directionX, directionY, directionZ));
                }
            });
            npcConfig.set("is-loaded", true);
            NPCConfig.save();
        }

        // Show NPCs
        npcManager.showQuestNPCs(player);
    }
}