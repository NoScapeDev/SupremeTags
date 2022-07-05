package net.noscape.project.supremetags.handlers.menu;

import org.bukkit.entity.*;

public class MenuUtil {

    private Player owner;
    private String identifier;

    public MenuUtil(Player owner, String identifier) {
        this.owner = owner;
        this.identifier = identifier;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
