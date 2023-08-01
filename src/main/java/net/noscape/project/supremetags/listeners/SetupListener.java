package net.noscape.project.supremetags.listeners;

import net.noscape.project.supremetags.SupremeTagsPremium;
import net.noscape.project.supremetags.handlers.SetupTag;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.PlayerConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.noscape.project.supremetags.utils.Utils.*;

public class SetupListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        SetupTag setup = SupremeTagsPremium.getInstance().getSetupList().get(player);

        if (setup == null) {
            return; // Player is not in the setup process, exit early
        }

        int currentStage = setup.getStage();

        if (currentStage == 1) {
            handleIdentifierInput(player, setup, event.getMessage());
            event.setCancelled(true);
        } else if (currentStage == 2) {
            handleTagInput(player, setup, event.getMessage());
            event.setCancelled(true);
        }
    }

    private void handleIdentifierInput(Player player, SetupTag setup, String message) {
        boolean isBannedWord = false;

        for (String word : SupremeTagsPremium.getInstance().getBannedWords().getBannedConfig().getStringList("banned-words")) {
            if (isWordBlocked(message, word)) {
                isBannedWord = true;
                break;
            }
        }

        if (message.equalsIgnoreCase("cancel")) {
            SupremeTagsPremium.getInstance().getSetupList().remove(player);
            msgPlayer(player, "&6&lStage: &7Cancelled setup process.");
            return;
        }

        if (!isBannedWord) {
            setup.setIdentifier(deformat(message));
            setup.setStage(2);
            String stage_two = SupremeTagsPremium.getInstance().getConfig().getString("messages.stages.stage-2");
            msgPlayer(player, stage_two);
        } else {
            msgPlayer(player, "&8[&6&lTags&8] &cMessage contains a bad word! Please try again.");
        }
    }

    private void handleTagInput(Player player, SetupTag setup, String message) {
        boolean isBannedWord = false;

        for (String word : SupremeTagsPremium.getInstance().getBannedWords().getBannedConfig().getStringList("banned-words")) {
            if (isWordBlocked(message, word)) {
                isBannedWord = true;
                break;
            }
        }

        if (message.equalsIgnoreCase("cancel")) {
            SupremeTagsPremium.getInstance().getSetupList().remove(player);
            msgPlayer(player, "&6&lStage: &7Cancelled setup process.");
            return;
        }

        if (!isBannedWord) {
            if (setup.isIdentifierSet() && !setup.isTagSet()) {
                setup.setTag(message);
                handleTagComplete(player, setup);
            } else {
                // If the player hasn't set the identifier yet, request it again
                msgPlayer(player, "&6&lStage 2: &7Please set the identifier first. &6&o(type in normal chat)");
            }
        } else {
            msgPlayer(player, "&8[&6&lTags&8] &cMessage contains a bad word! Please try again.");
        }
    }

    private void handleTagComplete(Player player, SetupTag setup) {
        List<String> tagList = new ArrayList<>();

        String t = setup.getTag();

        if (!player.hasPermission("supremetags.mytags.color")) {
            t = deformat(t);
        }

        String replace_tag = SupremeTagsPremium.getInstance().getConfig().getString("settings.personal-tags.format-replace").replace("%tag%", t);

        tagList.add(replace_tag);

        // Save the tag and perform any necessary actions
        Tag tag = new Tag(setup.getIdentifier(), tagList, "");
        SupremeTagsPremium.getInstance().getPlayerManager().addTag(player, tag);
        PlayerConfig.save(player);
        SupremeTagsPremium.getInstance().getPlayerManager().load(player);

        String stage_complete = SupremeTagsPremium.getInstance().getConfig().getString("messages.stages.complete");

        stage_complete = stage_complete.replaceAll("%identifier%", setup.getIdentifier());
        stage_complete = stage_complete.replaceAll("%tag%", setup.getTag());

        msgPlayer(player, stage_complete);
        SupremeTagsPremium.getInstance().getSetupList().remove(player);
    }

    private boolean isWordBlocked(String message, String blockedWord) {
        String pattern = "\\b" + blockedWord + "\\b";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(message);

        return matcher.find();
    }
}