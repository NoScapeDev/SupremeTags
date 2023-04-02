package net.noscape.project.supremetags.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TagAssignEvent extends Event implements Cancellable {

    private final Player player;
    private String tag;
    private double getCost;
    private boolean isCancelled;


    private static final HandlerList handlers = new HandlerList();

    public TagAssignEvent(Player player, String tag, double getCost, boolean isCancelled) {
        this.player = player;
        this.tag = tag;
        this.getCost = getCost;
        this.isCancelled = isCancelled;
    }

    public TagAssignEvent(Player player, String tag, boolean isCancelled) {
        this.player = player;
        this.tag = tag;
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    public void setTag(String tag) {
        this.tag = tag;
    }
}