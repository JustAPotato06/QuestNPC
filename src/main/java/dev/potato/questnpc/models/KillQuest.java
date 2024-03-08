package dev.potato.questnpc.models;

import org.bukkit.entity.EntityType;

public class KillQuest extends Quest {
    private EntityType entityType;
    private int amountToKill;
    private int progress = 0;

    public KillQuest(String name, String description, double rewardAmount, EntityType entityType, int amountToKill) {
        super(name, description, rewardAmount);
        this.entityType = entityType;
        this.amountToKill = amountToKill;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public int getAmountToKill() {
        return amountToKill;
    }

    public void setAmountToKill(int amountToKill) {
        this.amountToKill = amountToKill;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}