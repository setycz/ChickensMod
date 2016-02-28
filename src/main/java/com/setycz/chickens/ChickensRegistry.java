package com.setycz.chickens;

import java.util.*;

/**
 * Created by setyc on 12.02.2016.
 */
public final class ChickensRegistry {
    private static final Map<Integer, ChickensRegistryItem> items = new HashMap<Integer, ChickensRegistryItem>();
    private static Random rand = new Random();

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
        result.add(parent1);
        result.add(parent2);
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

    public static List<ChickensRegistryItem> getPossibleChickensToSpawn(SpawnType spawnType) {
        List<ChickensRegistryItem> result = new ArrayList<ChickensRegistryItem>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.canSpawn() && chicken.getSpawnType() == spawnType) {
                result.add(chicken);
            }
        }
        return result;
    }

    public static float getChildChance(ChickensRegistryItem child) {
        if (child.getTier() <= 1) {
            return 0;
        }

        List<ChickensRegistryItem> possibleChildren = getChildren(child.getParent1(), child.getParent2());

        int maxChance = getMaxChance(possibleChildren);
        int maxDiceValue = getMaxDiceValue(possibleChildren, maxChance);

        return ((maxChance - child.getTier()) * 100.0f) / maxDiceValue;
    }

    public static ChickensRegistryItem getRandomChild(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        List<ChickensRegistryItem> possibleChildren = getChildren(parent1, parent2);

        int maxChance = getMaxChance(possibleChildren);
        int maxDiceValue = getMaxDiceValue(possibleChildren, maxChance);

        int diceValue = rand.nextInt(maxDiceValue);
        return getChickenToBeBorn(possibleChildren, maxChance, diceValue);
    }

    private static ChickensRegistryItem getChickenToBeBorn(List<ChickensRegistryItem> possibleChildren, int maxChance, int diceValue) {
        int currentVale = 0;
        for (ChickensRegistryItem child : possibleChildren) {
            currentVale += maxChance - child.getTier();
            if (diceValue < currentVale) {
                return child;
            }
        }
        return null;
    }

    private static int getMaxDiceValue(List<ChickensRegistryItem> possibleChildren, int maxChance) {
        int maxDiceValue = 0;
        for (ChickensRegistryItem child : possibleChildren) {
            maxDiceValue += maxChance - child.getTier();
        }
        return maxDiceValue;
    }

    private static int getMaxChance(List<ChickensRegistryItem> possibleChildren) {
        int maxChance = 0;
        for (ChickensRegistryItem child : possibleChildren) {
            maxChance = Math.max(maxChance, child.getTier());
        }
        maxChance += 1;
        return maxChance;
    }
}
