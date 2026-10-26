package com.setycz.chickens;

import java.util.Locale;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;

public final class Breed {
    public final int legacyId;
    public final String id, name;
    public final Identifier texture;
    public Item layItem, dropItem;
    public Breed parent1, parent2;
    public Breeds.SpawnType spawn = Breeds.SpawnType.NORMAL;
    public boolean enabled = true;
    public double layCoefficient = 1;
    public String dyeColor;
    public EntityType<ResourceChicken> entityType;
    public Item spawnEgg;

    public Breed(int id, String name, Identifier texture, Item lay, int bg, int fg) {
        this(id, name, texture, lay, bg, fg, null, null);
    }
    public Breed(int id, String name, Identifier texture, Item lay, int bg, int fg, Breed p1, Breed p2) {
        this.legacyId = id;
        this.name = name.replace("Chicken", " Chicken").replace("LightBlue", "Light Blue").replace("SilverDye", "Light Gray").replace("Netherwart", "Nether Wart");
        this.id = name.replace("Chicken", "").replace("SilverDye", "light_gray").replace("LightBlue", "light_blue").toLowerCase(Locale.ROOT);
        this.texture = Identifier.fromNamespaceAndPath(texture.getNamespace(), texture.getPath().toLowerCase(Locale.ROOT));
        this.layItem = lay; this.parent1 = p1; this.parent2 = p2;
    }
    public Breed dye(String color) { dyeColor=color; return this; }
    public Breed setParentsNew(Breed a, Breed b) { parent1=a; parent2=b; return this; }
    public Breed setSpawnType(Breeds.SpawnType type) { spawn=type; return this; }
    public Breed setDropItem(Item item) { dropItem=item; return this; }
    public int tier() { return parent1==null || parent2==null ? 1 : 1+Math.max(parent1.tier(),parent2.tier()); }
    public boolean enabled() { return enabled && (parent1==null || parent1.enabled()) && (parent2==null || parent2.enabled()); }
    public boolean childOf(Breed a,Breed b) { return parent1==a && parent2==b || parent1==b && parent2==a; }
    public ItemStack lay(int gain) { ItemStack s=new ItemStack(layItem); s.setCount(s.getCount()*(gain>=10 ? 3 : gain>=5 ? 2 : 1)); return s; }
}
