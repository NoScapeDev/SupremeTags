package net.noscape.project.supremetags.api;

import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SupremeTagsAPI {

    /**
     * Returns tag from identifier.
     * @param identifier
     * @return
     */
    public Tag getTag(String identifier) {
        return SupremeTagsPremium.getInstance().getTagManager().getTag(identifier);
    }

    /**
     * Returns the players tag.
     * @param uuid
     * @return
     */
    public Tag getPlayerTag(UUID uuid) {
        return SupremeTagsPremium.getInstance().getTagManager().getTag(UserData.getActive(uuid));
    }

    /**
     * Return weather or not if the player has a tag.
     * @param uuid
     * @return
     */
    public boolean hasTag(UUID uuid) {
        return !UserData.getActive(uuid).equalsIgnoreCase("none");
    }

    /**
     * Get all registered tags.
     * @return
     */
    public List<Tag> getAllTags() {
        return new ArrayList<>(SupremeTagsPremium.getInstance().getTagManager().getTags().values());
    }
}