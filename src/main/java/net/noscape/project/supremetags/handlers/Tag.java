package net.noscape.project.supremetags.handlers;

public class Tag {

    private String identifier;
    private String tag;

    public Tag(String identifier, String tag) {
        this.identifier = identifier;
        this.tag = tag;
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

}