package dev.potato.questnpc.models;

public abstract class Quest {
    private String name;
    private String description;
    private double rewardAmount;
    private long whenStarted; // Epoch Time

    public Quest(String name, String description, double rewardAmount) {
        this.name = name;
        this.description = description;
        this.rewardAmount = rewardAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(double rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public long getWhenStarted() {
        return whenStarted;
    }

    public void setWhenStarted(long whenStarted) {
        this.whenStarted = whenStarted;
    }
}