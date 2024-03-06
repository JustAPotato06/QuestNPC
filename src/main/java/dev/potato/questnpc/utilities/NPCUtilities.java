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
import java.util.UUID;

public class NPCUtilities {
    private static final NPCUtilities npcManager = new NPCUtilities();

    private ServerPlayer questNPC;

    public static NPCUtilities getNpcManager() {
        return npcManager;
    }

    public ServerPlayer getQuestNPC() {
        return questNPC;
    }

    public int getQuestNPCID() {
        ServerPlayer npc = npcManager.getQuestNPC();
        return npc == null ? 0 : npc.getId();
    }

    public void spawnQuestNPC(Player p) {
        // Player NMS Data
        CraftPlayer craftPlayer = (CraftPlayer) p;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        // NPC Data
        MinecraftServer server = serverPlayer.getServer();
        ServerLevel level = serverPlayer.serverLevel().getLevel();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "Jeff");
        ClientInformation clientInformation = ClientInformation.createDefault();

        // Creating the NPC
        questNPC = new ServerPlayer(server, level, gameProfile, clientInformation);

        // NPC Position & Location
        Location playerLocation = p.getLocation();
        questNPC.setPos(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());

        // NPC Connection
        SynchedEntityData npcData = questNPC.getEntityData();
        npcData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        try {
            Field field = questNPC.getClass().getDeclaredField("c");
            field.setAccessible(true);
            field.set(questNPC, serverPlayer.connection);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was an error establishing a non-null connection to the NPC!");
        }

        // NPC Show Packets
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, questNPC));
        connection.send(new ClientboundAddEntityPacket(questNPC));

        p.sendMessage(ChatColor.GREEN + "[QUEST NPC] You have created a new quest NPC!");
        p.sendMessage(ChatColor.GREEN + "[QUEST NPC] Right click to talk to him!");
    }
}