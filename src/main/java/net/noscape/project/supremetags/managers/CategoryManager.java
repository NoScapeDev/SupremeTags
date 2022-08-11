package net.noscape.project.supremetags.managers;

import net.noscape.project.supremetags.*;
import net.noscape.project.supremetags.handlers.*;

import java.util.*;

public class CategoryManager {

    private final Map<String, Category> catorgies = new HashMap<>();

    public CategoryManager() {}

    public void loadCategories() {

        catorgies.clear();

        for (String cats : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("categories")).getKeys(false)) {
            List<String> tags = new ArrayList<>();

            String name = null;

            // loop through tags and see if the category equals the category you are setting.
            for (String tag : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("tags")).getKeys(false)) {
                String cat = SupremeTags.getInstance().getConfig().getString("tags." + tag + ".category");

                if (cat != null) {
                    if (cats.contains(cat)) {
                        tags.add(tag);
                    }
                }

                name = tag;
            }

            Category category = new Category(name, tags);

            // loads category into map
            catorgies.put(category.getCategory(), category);
        }
    }

    public Map<String, Category> getCatorgies() {
        return catorgies;
    }
}
