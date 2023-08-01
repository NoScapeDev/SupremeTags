package net.noscape.project.supremetags.handlers;

import java.util.*;

public class Category {

    private String category;
    private List<String> tags;

    private boolean isCost;

    public Category(String category, List<String> tags, boolean isCost) {
        this.category = category;
        this.tags = tags;
        this.isCost = isCost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isCost() {
        return isCost;
    }

    public void setCost(boolean cost) {
        isCost = cost;
    }
}
