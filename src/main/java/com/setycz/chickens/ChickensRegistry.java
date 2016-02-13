package com.setycz.chickens;

import com.setycz.chickens.chicken.EntityChickensChicken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
public final class ChickensRegistry {
    private static final List<ChickensRegistryItem> items = new ArrayList<ChickensRegistryItem>();

    public static void register(ChickensRegistryItem entity) {
        items.add(entity);
    }

    public static ChickensRegistryItem getByIndex(int index) {
        return items.get(index);
    }

    public static List<ChickensRegistryItem> getItems() {
        return items;
    }

    public static List<ChickensRegistryItem> getChildrens(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        List<ChickensRegistryItem> result = new ArrayList<ChickensRegistryItem>();
        for (ChickensRegistryItem item : items) {
            if (item.isChildOf(parent1, parent2)) {
                result.add(item);
            }
        }
        return result;
    }

    public static int getChildIndex(ChickensRegistryItem chicken) {
        return items.indexOf(chicken);
    }
}
