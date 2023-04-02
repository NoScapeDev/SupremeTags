package net.noscape.project.supremetags.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TagBuyEvent extends Event implements Cancellable {

    private final Player player;
    private final String tag;
    private double getCost;
    private boolean isCancelled;

    public TagBuyEvent(Player player, String tag, double getCost, boolean isCancelled) {
        this.player = player;
        this.tag = tag;
        this.getCost = getCost;
        this.isCancelled = isCancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public double getCost() {
        return getCost;
    }
}