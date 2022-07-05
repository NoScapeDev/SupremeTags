package net.noscape.project.supremetags.managers;

public class PlayerManager {

    private String activeTag;

    public PlayerManager(String activeTag) {
        this.activeTag = activeTag;
    }

    public String getActiveTag() {
        return activeTag;
    }

    public void setActiveTag(String activeTag) {
        this.activeTag = activeTag;
    }
}
