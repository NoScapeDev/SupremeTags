package net.noscape.project.supremetags.handlers;

import net.noscape.project.supremetags.enums.EditingType;

public class Editor {

    private String identifier;
    private EditingType type;

    public Editor(String identifier, EditingType type) {
        this.identifier = identifier;
        this.type = type;
    }

    public EditingType getType() {
        return type;
    }

    public void setType(EditingType type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
