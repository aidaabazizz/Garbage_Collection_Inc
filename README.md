# FIT2099 Assignment (Semester 1, 2026)

```
 _______  _______  ______    _______  _______  _______  _______                          
|       ||   _   ||    _ |  |  _    ||   _   ||       ||       |                         
|    ___||  |_|  ||   | ||  | |_|   ||  |_|  ||    ___||    ___|                         
|   | __ |       ||   |_||_ |       ||       ||   | __ |   |___                          
|   ||  ||       ||    __  ||  _   | |       ||   ||  ||    ___|                         
|   |_| ||   _   ||   |  | || |_|   ||   _   ||   |_| ||   |___                          
|_______||__| |__||___|  |_||_______||__| |__||_______||_______|                         
 _______  _______  ___      ___      _______  _______  _______  ___   _______  __    _   
|       ||       ||   |    |   |    |       ||       ||       ||   | |       ||  |  | |  
|       ||   _   ||   |    |   |    |    ___||       ||_     _||   | |   _   ||   |_| |  
|       ||  | |  ||   |    |   |    |   |___ |       |  |   |  |   | |  | |  ||       |  
|      _||  |_|  ||   |___ |   |___ |    ___||      _|  |   |  |   | |  |_|  ||  _    |  
|     |_ |       ||       ||       ||   |___ |     |_   |   |  |   | |       || | |   |  
|_______||_______||_______||_______||_______||_______|  |___|  |___| |_______||_|  |__|  
 ___   __    _  _______                                                                  
|   | |  |  | ||       |                                                                 
|   | |   |_| ||       |                                                                 
|   | |       ||       |                                                                 
|   | |  _    ||      _| ___                                                             
|   | | | |   ||     |_ |   |                                                            
|___| |_|  |__||_______||___|                                                                                                                                                                                                                         
```
---
**CONTRIBUTION LOG:**
https://docs.google.com/spreadsheets/d/1jF6rAykPxPmQ_06LkXPTXP32cgSiaoel0PHISt5tX1c/edit?usp=sharing

---

**REQUIREMENT 5 DETAILS**


In requirement 5, we have implemented two new creatures: **Elsa** and **Crazy Chicken**. 
These two creatures are spawned on the map through the Galaxy Portal and transition through different states, which are triggered by various factors such as worker action and proximity.

**☆ ELSA ☆**

Elsa is an ice-themed creature that emerges from Galaxy Portals. 
She has 40 HP and attacks with an Ice Blast that deals 3 damage with a 70% hit rate. 
Elsa is represented by the symbol ☆ on the map.


The following are Elsa's states and its effects:

- WANDERING: Default - wanders aimlessly 

- FREEZE: Freezes ALL workers for 2 turns (can't act)

- BLIZZARD: Disorients ALL workers for 3 turns (random movement)

- ICE_SPIKE: Creates 8 impassable spikes for 3 turns

- SINGING: Hypnotizes ALL slimes to attack workers for 5 turns 

---

These are the flow of the transitions.

WANDERING -> FREEZE (worker within 3 tiles)

WANDERING ->  BLIZZARD (≥5 workers on map)

WANDERING -> ICE_SPIKE (any worker ≤50% health)

WANDERING -> SINGING (slime within 5 tiles)

---

FREEZE -> ICE_SPIKE (consumable used)

FREEZE -> SINGING (adjacent slime)

FREEZE -> BLIZZARD (≥2 workers within 8 tiles)

FREEZE -> WANDERING (no worker within 3 tiles)

------

BLIZZARD -> ICE_SPIKE (consumable used)

BLIZZARD -> SINGING (adjacent slime)

BLIZZARD ->  FREEZE (worker within 3 tiles)

BLIZZARD ->  WANDERING (after 3 turns, <2 workers nearby)

---

ICE_SPIKE -> SINGING (adjacent slime)

ICE_SPIKE ->FREEZE (worker within 3 tiles)

ICE_SPIKE ->BLIZZARD (≥2 workers within 8 tiles)

ICE_SPIKE ->WANDERING (after 3 turns)


SINGING -> ICE_SPIKE (consumable used)

SINGING -> FREEZE (worker within 3 tiles)

SINGING -> BLIZZARD (≥2 workers within 8 tiles)

SINGING -> WANDERING (after 5 turns OR no adjacent slime)


---
**ก CRAZY CHICKEN ก**

CrazyChicken is a chaotic creature that emerges from Galaxy Portals. It has a sharp beak weapon that changes behavior based on its current state. The chicken is represented by the symbol ก on the map.

The following are Crazy Chicken's states and its effects:

- WANDER: Default - wanders aimlessly

- MIMICKING: Moves opposite direction of nearest worker, for example it will go west when worker goes east.

- FRENZY: Attacks workers and emits 'Shockwave', where  all workers within 8 tiles take 2 damage and gets pushed back 2 tiles.

- HUNGRY:Steals consumables from workers AND pulls all consumable items within 5 tiles to chicken

---
These are the flow of the transitions.

WANDER -> HUNGRY (adjacent worker has consumable)
WANDER -> MIMICKING (worker within 5 tiles)

---

MIMICKING -> HUNGRY (adjacent worker has consumable)
MIMICKING -> WANDER (no worker within 5 tiles)
MIMICKING -> FRENZY (after 2 turns)

---

FRENZY -> WANDER (after 3 turns, if actor nearby does not have consumable)
FRENZY -> HUNGRY (after 3 turns, if actor nearby has consumable)
---

HUNGRY -> MIMICKING (worker within 5 tiles)
HUNGRY -> WANDER (no adjacent worker with consumable)

---
Other notable features:
- Galaxy Portal (P)	Spawns Elsa or CrazyChicken every 20 turns (50/50 chance)
- Ice Spike (▲)	Temporary impassable ground, lasts 3 turns
- FrozenStatus	Prevents all actions for 2 turns
- BlizzardDisorientationStatus mirrors the worker's movement for 3 turns

- When Elsa sings, all slimes on the map become hypnotized for 5 turns. Hypnotized slimes check adjacent tiles and, if a worker is found, they swallow the worker—removing them from the map. Swallowed workers will be spat out onto an adjacent tile with remaining HP of 1. Workers with 3 HP or less will die inside the slime and never return.

