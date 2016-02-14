package com.setycz.chickens;

import java.util.*;

/**
 * Created by setyc on 12.02.2016.
 */
public final class ChickensRegistry {
    private static final Map<Integer, ChickensRegistryItem> items = new HashMap<Integer, ChickensRegistryItem>();

    public static void register(ChickensRegistryItem entity) {
        items.put(entity.getId(), entity);
    }

    public static ChickensRegistryItem getByType(int type) {
        return items.get(type);
    }

    public static Collection<ChickensRegistryItem> getItems() {
        return items.values();
    }

    public static List<ChickensRegistryItem> getChildren(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        List<ChickensRegistryItem> result = new ArrayList<ChickensRegistryItem>();
        for (ChickensRegistryItem item : items.values()) {
            if (item.isChildOf(parent1, parent2)) {
                result.add(item);
            }
        }
        return result;
    }

    public static ChickensRegistryItem findDyeChicken(int dyeMetadata) {
        for (ChickensRegistryItem chicken : items.values()) {
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
