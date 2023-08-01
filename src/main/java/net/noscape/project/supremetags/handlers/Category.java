package net.noscape.project.supremetags.handlers;

import java.util.*;

public class Category {

    private String category;
    private List<String> tags;

    public Category(String category, List<String> tags) {
        this.category = category;
        this.tags = tags;
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
}
