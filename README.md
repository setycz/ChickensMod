# Chickens

Chickens lay eggs. Sometimes drop feather. You can throw eggs. 
That's it. SO BOOORING! 
Minecraft is a magical place so why not to add a little bit of magic? 
Chicken laying gunpowder... bonemeal... snowballs... YEAH! 
And what if you try to crossbreed them? Iron? Gold? DIAMODS? Just try it!

Don't forget to visit [the project site](https://minecraft.curseforge.com/projects/chickens).

## Crossbreeding

TBD

## Chicken stats

Chickens have different stats which affects their abilities. 
Every stat is on scale from 1 (the lowest) to 10 (the highest).
Naturally spawned chickens always start with 1 and you can increase stats by [breeding](#crossbreeding) them but only if it has parents of the same breed.
Please make a note, that stats are not-increased when a new breed is created.

### Growth

The speed of which the chicken grows, lays and is able to reproduce. 
Having 10 means that it's ten times faster.

### Gain

Affects the amount of items (eggs) the chicken will lay:
* __1 - 4__ : 1 item
* __5 - 9__ : 2 items
* __10__ : 3 items at once !!!

### Strength

The ability for a chicken to carry its stats over to the children chickens, i.e. the chicken will most likely has stats based on the stronger parent.

## Tips

You can spawn chicken with desired stats by command:
```
/give @p chickens:spawn_egg 1 201 {Analyzed:1,Strength:10,Gain:10,Growth:10}
```
where ```201``` is chicken ID.
  
## Addon developers

Please assign yourself some high random offset for your chicken IDs in order to avoid conflict with other contributors.
E.g. 1 000 000 (your addon base ID) + 5 (your amazing fire breathing chicken) = 1 000 005.