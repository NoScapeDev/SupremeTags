package net.noscape.project.supremetags.managers;

import lombok.Getter;
import net.noscape.project.supremetags.*;

import java.util.*;

@Getter
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

        for (String cats : getCatorgies()) {
            int value = 0;
            for (String tags : Objects.requireNonNull(SupremeTags.getInstance().getConfig().getConfigurationSection("tags")).getKeys(false)) {
                String cat = SupremeTags.getInstance().getConfig().getString("tags." + tags + ".category");
                if (cats.equals(cat)) {
                    value++;
                }
            }
            catorgiesTags.put(cats, value);
        }
    }
}
