package dev.potato.questnpc.utilities;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCUtilities {
    private static final NPCUtilities npcManager = new NPCUtilities();

    private List<ServerPlayer> questNPCs = new ArrayList<>();

    public static NPCUtilities getNpcManager() {
        return npcManager;
    }

    public List<ServerPlayer> getQuestNPCs() {
        return questNPCs;
    }

    public void setQuestNPCs(List<ServerPlayer> questNPCs) {
        this.questNPCs = questNPCs;
    }

    public int getQuestNPCID(String displayName) {
        List<ServerPlayer> questNPCs = npcManager.getQuestNPCs();
        for (ServerPlayer npc : questNPCs) {
            if (!npc.displayName.equalsIgnoreCase(displayName)) continue;
            return npc.getId();
        }
        return 0;
    }

    public void spawnQuestNPC(Player p, String displayName) {
        // Player NMS Data
        CraftPlayer craftPlayer = (CraftPlayer) p;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        // NPC Data
        MinecraftServer server = serverPlayer.getServer();
        ServerLevel level = serverPlayer.serverLevel().getLevel();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), displayName);
        ClientInformation clientInformation = ClientInformation.createDefault();

        // Creating the NPC
        assert server != null;
        ServerPlayer npc = new ServerPlayer(server, level, gameProfile, clientInformation);

        // NPC Connection
        SynchedEntityData npcData = npc.getEntityData();
        npcData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        try {
            Field field = npc.getClass().getDeclaredField("c");
            field.setAccessible(true);
            field.set(npc, serverPlayer.connection);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was an error establishing a non-null connection to the NPC!");
        }

        // NPC Position & Location
        Location playerLocation = p.getLocation();
        npc.forceSetPositionRotation(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), playerLocation.getYaw(), playerLocation.getPitch());

        // Packets
        Bukkit.getOnlinePlayers().forEach(player -> {
            sendShowNPCPackets(player, npc);
        });

        // Add NPC to Quest NPC List
        questNPCs.add(npc);

        // Notify the player
        p.sendMessage(ChatColor.GREEN + "[QUEST NPC] You have created a new quest NPC!");
        p.sendMessage(ChatColor.GREEN + "[QUEST NPC] Right click to talk to him!");
    }

    public void loadQuestNPC(Player p, String uuid, String displayName, double x, double y, double z, float yaw, float pitch) {
        // Player NMS Data
        CraftPlayer craftPlayer = (CraftPlayer) p;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        // NPC Data
        MinecraftServer server = serverPlayer.getServer();
        ServerLevel level = serverPlayer.serverLevel().getLevel();
        GameProfile gameProfile = new GameProfile(UUID.fromString(uuid), displayName);
        ClientInformation clientInformation = ClientInformation.createDefault();

        // Creating the NPC
        assert server != null;
        ServerPlayer npc = new ServerPlayer(server, level, gameProfile, clientInformation);

        // NPC Connection
        SynchedEntityData npcData = npc.getEntityData();
        npcData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        try {
            Field field = npc.getClass().getDeclaredField("c");
            field.setAccessible(true);
            field.set(npc, serverPlayer.connection);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was an error establishing a non-null connection to the NPC!");
        }

        // NPC Position & Location
        npc.forceSetPositionRotation(x, y, z, yaw, pitch);

        // Packets
        Bukkit.getOnlinePlayers().forEach(player -> {
            sendShowNPCPackets(player, npc);
        });

        // Add NPC to Quest NPC List
        questNPCs.add(npc);
    }

    public void showQuestNPCs(Player player) {
        for (ServerPlayer npc : questNPCs) {
            sendShowNPCPackets(player, npc);
        }
    }

    private void sendShowNPCPackets(Player player, ServerPlayer npc) {
        // NPC Show Packets
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
        connection.send(new ClientboundAddEntityPacket(npc));
    }
}