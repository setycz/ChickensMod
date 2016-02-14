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

    public static ChickensRegistryItem getByType(int type) {
        return items.get(type);
    }

    public static List<ChickensRegistryItem> getItems() {
        return items;
    }

    public static List<ChickensRegistryItem> getChildren(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        List<ChickensRegistryItem> result = new ArrayList<ChickensRegistryItem>();
        for (ChickensRegistryItem item : items) {
            if (item.isChildOf(parent1, parent2)) {
                result.add(item);
            }
        }
        return result;
    }

    public static int getType(ChickensRegistryItem chicken) {
        return items.indexOf(chicken);
    }

    public static ChickensRegistryItem findDyeChicken(int dyeMetadata) {
        for (ChickensRegistryItem chicken : items) {
            if (chicken.isDye(dyeMetadata)) {
                return chicken;
            }
        }
        return null;
    }

    public static List<ChickensRegistryItem> getPossibleChickensToSpawn() {
        List<ChickensRegistryItem> result = new ArrayList<ChickensRegistryItem>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.canSpawn()) {
                result.add(chicken);
            }
        }
        return result;
    }
}
