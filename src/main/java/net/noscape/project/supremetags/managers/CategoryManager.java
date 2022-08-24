package net.noscape.project.supremetags.managers;

import net.noscape.project.supremetags.*;

import java.util.*;

public class CategoryManager {

    private final List<String> catorgies = new ArrayList<>();

    public CategoryManager() {}

    public void loadCategories() {
        catorgies.clear();
        catorgies.addAll(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("categories")).getKeys(false));
    }

    public List<String> getCatorgies() {
        return catorgies;
    }
}
