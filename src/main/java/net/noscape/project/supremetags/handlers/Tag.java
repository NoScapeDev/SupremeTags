package net.noscape.project.supremetags.handlers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public Tag(String identifier, String tag, String description) {
        this.identifier = identifier;
        this.tag = tag;;
        this.description = description;
    }
}