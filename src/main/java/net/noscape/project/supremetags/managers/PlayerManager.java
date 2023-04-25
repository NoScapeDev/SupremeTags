package net.noscape.project.supremetags.managers;

import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.PlayerConfig;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, Tag> playerTags = new HashMap<>();

    public PlayerManager() {}

    public List<Tag> getPlayerTags(UUID uuid) {
        List<Tag> tag_list = new ArrayList<>();

        for (Tag t : playerTags.values()) {
            if (playerTags.get(uuid) == null) break;

            if (playerTags.get(uuid) == t) {
                tag_list.add(t);
            }
        }

        return tag_list;
    }

    public void load(Player player) {
        playerTags.remove(player.getUniqueId());

        if (PlayerConfig.get(player).getConfigurationSection("tags") != null) {
            for (String identifier : PlayerConfig.get(player).getConfigurationSection("tags").getKeys(false)) {
                String tag = PlayerConfig.get(player).getString("tags." + identifier + ".tag");
                String description = PlayerConfig.get(player).getString("tags." + identifier + ".description");

                Tag t = new Tag(identifier, tag, description);
                playerTags.put(player.getUniqueId(), t);
            }
        }
    }

    public Map<UUID, Tag> getPlayerTags() {
        return playerTags;
    }

    public void delete(Player player, String identifier) {
        PlayerConfig.get(player).set("tags." + identifier, null);
        SupremeTags.getInstance().getPlayerConfig().save(player);
    }

    public void save(Tag tag, Player player) {
        PlayerConfig.get(player).set("tags." + tag.getIdentifier() + ".tag", tag.getTag());
        PlayerConfig.get(player).set("tags." + tag.getIdentifier() + ".description", tag.getDescription());
        SupremeTags.getInstance().getPlayerConfig().save(player);
    }
}