package dev.potato.questnpc.models;

import org.bukkit.Material;

public class ItemQuest extends Quest {
    private Material material;
    private int amountToCollect;
    private int progress = 0;

    public ItemQuest(String name, String description, double rewardAmount, Material material, int amountToCollect) {
        super(name, description, rewardAmount);
        this.material = material;
        this.amountToCollect = amountToCollect;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmountToCollect() {
        return amountToCollect;
    }

    public void setAmountToCollect(int amountToCollect) {
        this.amountToCollect = amountToCollect;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}