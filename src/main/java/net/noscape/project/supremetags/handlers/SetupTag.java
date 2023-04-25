package net.noscape.project.supremetags.handlers;

public class SetupTag {

    private String identifier; // stage 1

    private String tag; // stage 2

    private int stage;

    public SetupTag(int stage) {
        this.identifier = null;
        this.tag = null;
        this.stage = stage;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isTagSet() {
        return tag != null;
    }

    public boolean isIdentifierSet() {
        return identifier != null;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
