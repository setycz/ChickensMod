package com.setycz.chickens;
import java.util.*;
import java.util.random.RandomGenerator;

/** Vanilla-independent breeding math, shared with the regression checks. */
public final class Breeding {
    private Breeding() {}
    public static int stat(int strength1,int strength2,int stat1,int stat2,int mutation) {
        return Math.clamp((stat1*strength1+stat2*strength2)/(strength1+strength2)+mutation,1,10);
    }
    public static Breed choose(Breed a,Breed b,net.minecraft.util.RandomSource random) {
        List<Breed> options=new ArrayList<>();
        if(a.enabled()) options.add(a);
        if(b!=a && b.enabled()) options.add(b);
        for(Breed c:Breeds.ALL.values()) if(c.enabled() && c.childOf(a,b)) options.add(c);
        if(options.isEmpty()) return a;
        int max=options.stream().mapToInt(Breed::tier).max().orElse(1)+1;
        int roll=random.nextInt(options.stream().mapToInt(c->max-c.tier()).sum());
        for(Breed c:options) { roll-=max-c.tier(); if(roll<0) return c; }
        return a;
    }
}
