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
### ASSIGNMENT 2

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

### ASSIGNMENT 3 

## REQ5: Real Weather Anomaly System

REQ5 uses the OpenWeather API to bring real-world weather into the game. The worker can trigger this feature through the SuperComputer. The API data is not only displayed; it is used to activate weather anomalies that can modify terrain, hazards, actors, and existing REQ3/REQ4 systems.

The three possible weather anomalies are:

* Conductive Rain
* Heat Distortion
* Storm Surge

### How to Get an OpenWeather API Key

1. Go to the OpenWeather website.
2. Create a free account or sign in.
3. Open the API keys section in the account dashboard.
4. Generate a new API key.
5. Copy the key.
6. Store it as an environment variable named `OPENWEATHER_API_KEY`.

Do not paste the real API key into the source code, README, or GitLab.

A new API key may take a short time to activate. If the first request fails immediately after creating the key, wait a few minutes and try again.

### API Key Setup

The API key must be stored in this environment variable:

```text
OPENWEATHER_API_KEY
```

For Windows PowerShell:

```powershell
$env:OPENWEATHER_API_KEY="your_api_key_here"
```

For Mac/Linux:

```bash
export OPENWEATHER_API_KEY="your_api_key_here"
```

In IntelliJ:

```text
Run Configuration > Environment variables
```

Add:

```text
OPENWEATHER_API_KEY=your_api_key_here
```

### How to Run REQ5

1. Set `OPENWEATHER_API_KEY`.
2. Run `game.Application`.
3. Move the worker next to the SuperComputer (`≡`).
4. Select the Weather Sync action.
5. The game will call the OpenWeather API and convert the response into a `WeatherSnapshot`.
6. If the weather meets an anomaly condition, the matching effect changes the game world.

### Dynamic API Request

The request is built by `WeatherQuery`, so it is not a fixed static URL. It uses game-state information such as the current map, `WeatherMapAnchor`, actor location, metric units, and the API key.

Example request format:

```text
https://api.openweathermap.org/data/2.5/weather?lat=-27.4705&lon=153.0260&appid=${OPENWEATHER_API_KEY}&units=metric
```

### REQ5 Structure

Main abstractions:

* `WeatherAnomalyInterpreter`
* `AnomalyWorldEffect`

Interpreter implementations:

* `HumidityAnomalyInterpreter`
* `TemperatureAnomalyInterpreter`
* `StormAnomalyInterpreter`

World effect implementations:

* `ConductiveRainEffect`
* `HeatDistortionEffect`
* `StormSurgeEffect`

Higher-level classes using the abstractions:

* `WeatherSyncAction`
* `WeatherAnomalyManager`

API support classes:

* `WeatherApiClient`
* `WeatherQuery`
* `WeatherSnapshot`
* `WeatherConfig`
* `WeatherApiException`
* `WeatherSystemFactory`

### Weather Effects

Conductive Rain:

* creates `Puddle` terrain
* suppresses `Extinguishable` hazards
* triggers `ChargeReactive` grounds

Heat Distortion:

* strengthens `FireStackable` hazards
* creates `BlueFire` near distorted or sanctuary-related areas

Storm Surge:

* applies `GalvanicCharge`
* triggers or creates `AtmosphericChargeSource`
* may damage actors
* may spread `RageGround`

Weather effects protect important terrain such as the SuperComputer using capability checks such as `FacilityCapability`, `MaterialCapability`, and `DistortionCapability`.

### Testing

REQ5 unit tests were added in:

```text
src/test/java/game/weather/WeatherQueryTest.java
src/test/java/game/weather/WeatherSnapshotTest.java
src/test/java/game/weather/interpreters/WeatherAnomalyInterpreterTest.java
```

The tests cover:

* dynamic URL construction
* storing parsed weather data
* case-insensitive weather condition checking
* humidity anomaly detection
* heat anomaly detection
* storm anomaly detection
* mild weather not activating any interpreter

Latest Maven test result:

```text
Tests run: 84, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Security Notes

* The actual API key must not be committed to GitLab.
* The API key must not be hardcoded in the source code.
* The README only uses the placeholder `your_api_key_here`.
* The API key is read through `WeatherConfig`.
