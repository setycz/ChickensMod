package com.setycz.chickens;

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
}
