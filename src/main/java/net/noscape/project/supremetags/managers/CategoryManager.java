package net.noscape.project.supremetags.managers;

import net.noscape.project.supremetags.*;

import java.util.*;

public class CategoryManager {

    private final List<String> catorgies = new ArrayList<>();

    private final Map<String, Integer> catorgiesTags = new HashMap<>();

    public CategoryManager() {}

    public void loadCategories() {
        catorgies.clear();
        catorgies.addAll(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("categories")).getKeys(false));
    }

    public void loadCategoriesTags() {

        catorgiesTags.clear();

        int value = 0;
        for (String cats : getCatorgies()) {
            for (String tags : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("tags")).getKeys(false)) {
                if (Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("tags." + tags + ".category")).equalsIgnoreCase(cats)) {
                    value++;
                }

                catorgiesTags.put(cats, value);
            }
        }
    }

    public List<String> getCatorgies() {
        return catorgies;
    }

    public Map<String, Integer> getCatorgiesTags() {
        return catorgiesTags;
    }
}
