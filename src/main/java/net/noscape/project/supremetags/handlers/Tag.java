package net.noscape.project.supremetags.handlers;

public class Tag {

    private String identifier;
    private String tag;
    private String category;
    private String permission;
    private String description;
    private double cost;

    public Tag(String identifier, String tag, String category, String permission, String description, double cost) {
        this.identifier = identifier;
        this.tag = tag;
        this.category = category;
        this.permission = permission;
        this.description = description;
        this.cost = cost;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}