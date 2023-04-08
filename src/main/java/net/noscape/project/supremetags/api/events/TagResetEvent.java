package net.noscape.project.supremetags.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TagResetEvent extends Event implements Cancellable {

    private final Player player;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    public TagResetEvent(Player player, boolean isCancelled) {
        this.player = player;
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
}