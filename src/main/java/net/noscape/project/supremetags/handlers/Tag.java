package net.noscape.project.supremetags.handlers;

public class Tag {

    private String identifier;
    private String tag;
    private String category;
    private String permission;

    public Tag(String identifier, String tag, String category, String permission) {
        this.identifier = identifier;
        this.tag = tag;
        this.category = category;
        this.permission = permission;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}