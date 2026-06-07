# Feature Proposal — REQ3 & REQ4: Creative Mode

---

## REQ3: High-Voltage Galvanic System

### The Pitch

The High-Voltage Galvanic System introduces a persistent, cross-component electrical ecosystem
to the moon facility. Conductive energy sources (Emitters) release high-voltage surges that
cascade through terrain, actors, and items simultaneously. This system transforms the environment
dynamically by electrifying puddles into hazards, evolving dormant creatures into predators,
powering magnetic tools to harvest scrap remotely, and paralysing workers caught in the current.

### The Mechanics

- **Emitters** (`AtmosphericChargeSource`, `TeslaCoil`, `PortableBattery`) release `GalvanicCharge`
  pulses into the map via `ChargeSource.releaseCharge(location, charge)`.
- **AtmosphericChargeSource** fires a random lightning strike anywhere on the map each turn
  with a 10% probability, initiating a cascading surge at the struck coordinate.
- **TeslaCoil** charges over 3 turns and auto-discharges a diamond-shaped (Manhattan Distance ≤ 2)
  AoE blast; it can also be manually triggered by an adjacent actor via `GalvanicSurgeAction`,
  or overloaded by an incoming charge from another source.
- **PortableBattery** is a single-use "Strategic Trigger" that can be activated either from the actor's 
  inventory or while lying on a map tile. This allows the player to use it as a handheld tool for
  exploration or plant it strategically as a remote-access environmental trap. On activation, it
  permanently converts the ground to a `PoweredFloor`, spawns `IonizedBarrier` walls on all 8 surrounding tiles
  to trap enemies, and zaps every actor in the radius.
- **Resonators** (`Puddle`, `ElectrifiedPuddle`, `PoweredFloor`, `IonizedBarrier`,
  `DormantStaticCreature`, `Wallet`) implement `ChargeReactive.reactToCharge(location, charge)`
  to define their response:
  - `Puddle` → Performs a multi-stage reaction: uses `ChargeUtils` to zap occupants immediately
    upon impact before undergoing a structural map change into an `ElectrifiedPuddle`.
  - `ElectrifiedPuddle` → stacks its lifespan (up to 3× base value) when re-struck, continuously
    damages actors standing on it each turn, and arcs `ParalyzedStatus` (20% chance) to all
    8 adjacent actors.
  - `PoweredFloor` → propagates the incoming charge to all 8 neighbouring tiles and zaps any
    occupants found there, acting as a permanent conductive bridge.
  - `IonizedBarrier` → blocks all actor passage (`canActorEnter` returns `false`), magnetically
    locks all `MAGNETIC` items in a 3×3 area around it, and reinforces itself (+3 turns lifespan)
    if struck again by an incoming charge.
  - `DormantStaticCreature` → accumulates charge in an internal `anomalyRadius` counter (0→3);
    at maximum radius, triggers a magnetic repulsion pulse that scans a diamond-shaped area
    (Manhattan distance ≤ 3) and physically repels all `MAGNETIC` items 2 tiles away, then
    irreversibly evolves into a `StaticStalker`.
  - `Wallet` → activates as an electromagnet, pulling all `MAGNETIC` items within a 5×5 grid
    (subject to wall-based ray-cast flux blocking via `isPathBlocked`) into the holder's
    inventory or to their feet if the inventory is full.
- **StaticStalker** (evolved predator) emits a passive "Static Aura" every turn:
  heals itself when standing on `ENERGIZED` terrain (Actor-Ground synergy), morphs adjacent
  Puddles into `ElectrifiedPuddle` hazards, applies `ParalyzedStatus` (20%, or 40% when
  overcharged) to neighbouring actors, and magnetically triggers `ChargeReactive` items in
  adjacent inventories.
- **ParalyzedStatus** forces the affected actor to skip their turn and simultaneously grants
  `MaterialCapability.REFLECTIVE`, causing any attacker to suffer 1 reflective damage and
  receive `ShockedStatus` (electrical arc-back processed in `AttackAction`).
- **ShockedStatus** extends `DamageOverTimeStatus`; while active it marks the host as
  `CONDUCTIVE` and releases a 1-tile AoE pulse every turn, triggering ground and actor
  reactions on all 8 neighbours (the "Human Lightning Bolt" effect).
- **GalvanicCharge** carries a visited `HashSet<Location>` to prevent infinite recursion
  between adjacent conductive tiles and ensures each entity is processed at most once per
  surge event.
- **MaterialCapability** enum (`ENERGIZED`, `MAGNETIC`, `MAGNETICALLY_LOCKED`, `CONDUCTIVE`,
  `PARALYZED`, `REFLECTIVE`, `MAGNETIC_REPULSIVE`) replaces all `instanceof` checks with a
  capability-tag system, strictly following the Dependency Inversion Principle.
- **MagneticItem** (abstract base for `CRTMonitor`, `FloppyDisk`, `Lantern`) automatically
  grants the `MAGNETIC` capability and overrides `allowableActions(Location)` to return an
  empty list while `MAGNETICALLY_LOCKED` is active, disabling manual pick-up.

### The Architecture

#### New Abstractions

| Abstraction | Type | Role |
|---|---|---|
| `ChargeSource` | Interface | Emitter contract — `releaseCharge` + default `consumeSource` |
| `ChargeReactive` | Interface | Resonator contract — `reactToCharge` |
| `ChargeContext` | Interface | Carrier contract — source name, display, damage, visited-set |
| `MagneticItem` | Abstract Class | Base for all `MAGNETIC` scrap items |

> Note: `ChargeSource` and `ChargeReactive` are the two primary new abstractions for REQ3.
> `ChargeContext` and `MagneticItem` are supporting interfaces that compose the full system.

#### Concrete Classes (REQ3 — minimum 6 required)

| Class | Status | Abstraction Implemented/Extended | Complex Effect                                                                                                                                                                                             |
|---|---|---|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GalvanicCharge` | **New** | `ChargeContext` | (HELPER) Carries surge metadata + visited-set memory for recursion prevention                                                                                                                              |
| `TeslaCoil` | **New** | `ChargeSource`, `ChargeReactive` | Temporal capacitor cycle, diamond AoE, Overload chain reactions                                                                                                                                            |
| `AtmosphericChargeSource` | **New** | `ChargeSource` | Random map-wide lightning strikes (10% chance, any coordinate) with cascading surge propagation                                                                                                            |
| `PortableBattery` | **New** | `ChargeSource` | Terrain morphing + IonizedBarrier spawning + AoE impact                                                                                                                                                    |
| `DormantStaticCreature` | **New** | `ChargeReactive` | Magnetic Anomaly: accumulates charge (0→3), triggers magnetic pulse at max radius, scans diamond-shaped area (Manhattan distance ≤ 3), repels MAGNETIC items 2 tiles away, then hatches into StaticStalker |
| `StaticStalker` | **New** | `NonPlayerCharacter` (extended) | Static Aura: ground synergy, terrain morphing, proximity stun, inventory induction                                                                                                                         |
| `Puddle` | **Retrofitted** (A1/A2) | `ChargeReactive` | Conducts immediate damage to occupants + Structural terrain morphing → ElectrifiedPuddle                                                                                                                   |
| `ElectrifiedPuddle` | **New** | `ChargeReactive` | Timed hazard, lifespan stacking, AoE paralysis arcing                                                                                                                                                      |
| `PoweredFloor` | **New** | `ChargeReactive` | Cascade propagation to 8 neighbours                                                                                                                                                                        |
| `IonizedBarrier` | **New** | `ChargeReactive` | Pathing blockage + magnetic locking of nearby items                                                                                                                                                        |
| `Wallet` | **Retrofitted** (A1/A2) | `ChargeReactive` | 5×5 AoE magnetic item harvesting with wall-based ray-cast flux blocking                                                                                                                                    |
| `MagneticItem` | **New** | Abstract class | Base for all magnetic scrap; disables pick-up when `MAGNETICALLY_LOCKED`                                                                                                                                   |
| `CRTMonitor` | **Retrofitted** (A1/A2) | `MagneticItem` | MAGNETIC capability + fire-spawning short-circuit on sale                                                                                                                                                  |
| `FloppyDisk` | **Retrofitted** (A1/A2) | `MagneticItem` | MAGNETIC capability + random credit-deduction glitch on sale                                                                                                                                               |
| `Lantern` | **Retrofitted** (A1/A2) | `MagneticItem` | MAGNETIC capability + oil-drain infection + fire-spread on sale                                                                                                                                            |

**Summary for REQ3 (satisfying the 6-concrete-class rule):**
- At least 3 entirely new classes: `TeslaCoil`, `AtmosphericChargeSource`, `PortableBattery`,
  `DormantStaticCreature`, `StaticStalker`, `ElectrifiedPuddle`, `PoweredFloor`, `IonizedBarrier`,
  `GalvanicCharge`, `MagneticItem` (all new).
- Retrofitted existing classes: `Puddle`, `Wallet`, `CRTMonitor`, `FloppyDisk`, `Lantern`
  — all modified with meaningful, complex logic as required by Rule 2.

> ## MARKER NOTE — Six Concrete Classes for REQ3 Assessment
>
> Per Rule 1 and Rule 2, the six concrete classes implementing the two abstractions (`ChargeSource` and `ChargeReactive`) are:
>
>
> | # | Class | Status | Abstraction Implemented | Complex Effect |
> |---|---|---|---|---|
> | 1 | `AtmosphericChargeSource` | **New** | `ChargeSource` | Random map-wide lightning strikes (10% chance, any coordinate) with cascading surge propagation via `ChargeUtils` |
> | 2 | `TeslaCoil` | **New** | `ChargeSource`, `ChargeReactive` | 3-turn capacitor cycle + diamond-shaped AoE (Manhattan Distance ≤ 2) + overload chain reactions between coils |
> | 3 | `PortableBattery` | **New** | `ChargeSource` | Single-use: center tile → permanent `PoweredFloor`; 8 neighbors → temporary `IonizedBarrier` walls; AoE zap of all occupants |
> | 4 | `DormantStaticCreature` | **New** | `ChargeReactive` | Magnetic Anomaly accumulation (0→3); at max radius triggers diamond-shaped (Manhattan ≤ 3) item repulsion pulse pushing `MAGNETIC` items 2 tiles away; then hatches into `StaticStalker` |
> | 5 | `Puddle` | **Retrofitted** (A1/A2) | `ChargeReactive` | Immediate occupant damage via `ChargeUtils.zapTile` + permanent structural terrain morphing into `ElectrifiedPuddle` |
> | 6 | `Wallet` | **Retrofitted** (A1/A2) | `ChargeReactive` | 5×5 AoE magnetic item harvesting with ray-cast wall-based flux blocking (`isPathBlocked`) and inventory weight validation |
>
> *Please focus your assessment on these six classes when evaluating REQ3.*
---

Here is your **REQ4: Distorted Sanctuary System** proposal, formatted exactly like your teammate's `.md` file to ensure consistency and professional presentation for your TA.

***

# Feature Proposal — REQ4: Distorted Sanctuary System

---

## REQ4: Distorted Sanctuary System

### The Pitch

The Distorted Sanctuary System introduces unstable spatial anomalies and high-tech protective zones to the moon facility. Spatiotemporal hazards (`DistortionSource`) release corruption that warps game physics, manipulates actor behavior, and forces random repositioning. To combat these threats, workers utilize specialized equipment (`SanctuaryTool`) and the facility’s `SuperComputer` to deploy holy protection fields, fire tactical remote knockback pulses, and perform corporate audits on anomalies to meet the company quota.

### The Mechanics

- **Distortion Sources** (`BlackHolePortal`, `CorruptedSafeHouse`, `RageGround`) implement `DistortionSource.releaseDistortion(actor, map, location)` to define their environmental impact:
  - `BlackHolePortal` → Applies a crushing `BlackHoleStatus` (reusing A2 `DamageOverTimeStatus`) that deals 1 damage and executes a map-wide random safe-tile teleportation at the start of every turn.
  - `CorruptedSafeHouse` → A presence-aware zone that grants `SanctuaryStatus` (healing + protection) to occupants while dynamically spawning a ring of hazardous `BlueFire` on all 8 adjacent tiles.
  - `RageGround` → A behavioral anomaly that injects `RageStrikeAction` (a high-damage lifesteal mutation) into the worker’s menu. It features a state-synced relocation lifecycle where the ground vanishes and rematerializes at random coordinates once its 3-turn energy is spent.
- **Sanctuary Tools** (`CommandWhistle`, `HeavenToken`, `SuperComputer`) implement `SanctuaryTool.activateSanctuaryEffect(actor, map, location)` to stabilize the environment:
  - `CommandWhistle` → Acts as a remote tactical relay; it performs a Manhattan-distance scan to find the nearest ally and centers a physics-based knockback pulse on *them*, blasting enemies 2 tiles away and dealing impact damage upon collision with walls or other actors.
  - `HeavenToken` → A consumable item that triggers immediate terrain mutation, replacing the floor with a 10-turn `SanctuaryField` that heals and protects everyone in its radius.
  - `SuperComputer` → Modified to act as a corporate sensor terminal; it performs a Radius-3 AoE scan to execute the **Distortion Audit Protocol**, which harvests data from anomalies to update the `QuotaManager` (REQ1 integration) and forces them into weakened cooldown states.
- **BlueFire** → A presence-aware timed hazard that implements the `FireStackable` interface. It intensifies in duration when refreshed by a SafeHouse and utilizes the **Memento Pattern** to restore the original terrain (Dirt, Floor, etc.) once the worker leaves the area or the fire burns out.
- **SanctuaryStatus** → Manages a complex lifecycle of presence-aware healing and protection. It provides the `PROTECTED` capability (intercepted in `ContractedWorker.hurt`) only while the worker is on the tile, and automatically self-destructs upon departure to allow a full reset on re-entry.
- **DistortionCapability** enum (`CORRUPTED`, `SANCTUARY`, `ACTIVE_HAZARD`) facilitates polymorphic interaction between the facility terminal and map hazards, strictly following the **Dependency Inversion Principle**.

---

### The Architecture

#### New Abstractions

| Abstraction | Type | Role |
|---|---|---|
| `DistortionSource` | Interface | Anomaly contract — `releaseDistortion` + `stabilise` + `audit` |
| `SanctuaryTool` | Interface | Tactical contract — `activateSanctuaryEffect` |

#### Concrete Classes (REQ4 — minimum 6 required)

| Class | Status | Abstraction Implemented | Complex Effect |
|---|---|---|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `BlackHolePortal` | **New** | `DistortionSource` | Spatiotemporal Warp: applies `BlackHoleStatus` for map-wide random teleportation + crushing damage. |
| `CorruptedSafeHouse` | **New** | `DistortionSource` | Dual-Zone Lifecycle: manages a presence-aware healing status while spawning/cleaning up a ring of `BlueFire` hazards. |
| `RageGround` | **New** | `DistortionSource` | Action Mutation: injects forbidden worker-vs-worker `RageStrikeAction` into the menu + executes Terrain Relocation once energy is spent. |
| `CommandWhistle` | **New** | `SanctuaryTool`, `Purchasable` | Tactical Relay: remote Manhattan-distance scan to trigger a physics-based knockback pulse centered on a distant ally. |
| `HeavenToken` | **New** | `SanctuaryTool`, `Purchasable` | Terrain Morphing: consumable item that replaces current ground with an AoE `SanctuaryField`. |
| `SuperComputer` | **Retrofitted** (A1/A2) | `SanctuaryTool` | Corporate Audit Protocol: Radius-3 AoE scan to harvest anomaly data, update `QuotaManager` credits, and weaken terrain hazards. |

> ## MARKER NOTE — Six Concrete Classes for REQ4 Assessment
>
> Per Rule 1 and Rule 2, the six concrete classes implementing the two abstractions (`DistortionSource` and `SanctuaryTool`) are:
>
> | # | Class | Status | Abstraction Implemented | Complex Effect |
> |---|---|---|---|---|
> | 1 | `BlackHolePortal` | **New** | `DistortionSource` | Ground → Status → Map-wide coordinate manipulation → Random repositioning + Crushing damage. |
> | 2 | `CorruptedSafeHouse` | **New** | `DistortionSource` | Presence-aware logic: Ground → Status + Synchronized AoE ground spawning (`BlueFire`) → Instant hazard cleanup on leave. |
> | 3 | `RageGround` | **New** | `DistortionSource` | Action Mutation: Ground → Status → Injected `RageStrikeAction` (Lifesteal) → Immediate terrain relocation to random safe coordinates. |
> | 4 | `CommandWhistle` | **New** | `SanctuaryTool` | Remote Tactical Relay: Item → Action → Manhattan distance ally scan → Remote physics-based knockback pulse with collision damage. |
> | 5 | `HeavenToken` | **New** | `SanctuaryTool` | Terrain Mutation: Item → Action → Physical replacement of floor with a 10-turn `SanctuaryField` (AoE heal/protect). |
> | 6 | `SuperComputer` | **Retrofitted** (A1/A2) | `SanctuaryTool` | Facility System: Ground → Action → Radius-3 AoE scan → Anomaly reaction → `QuotaManager` credit update → Terrain weakening. |
>
> *Please focus your assessment on these six classes when evaluating REQ4.*

---

### Supporting Classes (Logic & Integration)

| Class | Role |
|---|---|
| `BlackHoleStatus` | Logic handler for crushing damage and random map-wide teleportation (reusing A2 `DamageOverTimeStatus`). |
| `SanctuaryStatus` | Presence-aware lifecycle manager for the 5-turn protection and forever-healing rules. |
| `MotivationStatus` | Physics engine for the remote pulse: trajectory calculation and context-aware collision damage (Sarah/Walls/Boundaries). |
| `RageStrikeAction` | Custom high-damage lifesteal combat mutation injected into the worker's menu via `KillerInstinctStatus`. |
| `BlueFire` | Presence-aware timed hazard utilizing the Memento pattern for terrain restoration and `FireStackable` for damage intensification. |
| `SanctuaryField` | Temporary AoE ground tile that provides healing and `PROTECTED` capability in a radius. |
| `DistortionAuditAction` | Separate Action class provided by SuperComputer to execute the corporate scan and Quota integration. |
| `SpatialSearch` | Centralized utility for Manhattan distance, radial queries, and nearest-actor detection (DRY implementation). |

---

# Feature Proposal — REQ5: Real Weather Anomaly System

---

## REQ5: Real Weather Anomaly System

### The Pitch

The Real Weather Anomaly System connects the game world to the OpenWeather API and uses real-world weather data to create meaningful changes inside the moon facility. The API is not used only to display weather information to the player. Instead, the returned weather data is interpreted and used to trigger environmental anomalies that affect terrain, hazards, actors, and existing game systems.

The worker can activate this feature through the `SuperComputer`. When the weather sync action is selected, the game sends a dynamic API request based on the current game state. The returned weather conditions may trigger one or more anomalies, such as Conductive Rain, Heat Distortion, or Storm Surge.

This makes the game world feel less static because real-world weather conditions can influence what happens inside the facility.

---

### The Mechanics

* The worker stands near the `SuperComputer` and selects the weather sync option.
* `WeatherSyncAction` starts the weather system when the action is selected by the worker.
* The system builds a weather request using the current game state instead of using one fixed static URL.
* The current map and actor position help determine the real-world coordinates used in the API request.
* `WeatherMapAnchor` links the in-game map context to base real-world latitude and longitude values.
* `WeatherQuery` stores the request values such as latitude, longitude, API key, and metric units.
* `WeatherConfig` reads the OpenWeather API key from the `OPENWEATHER_API_KEY` environment variable.
* `WeatherApiClient` sends the HTTP request to OpenWeather.
* The raw API response is parsed and stored inside a `WeatherSnapshot`.
* `WeatherSnapshot` stores the useful weather values needed by the game, such as temperature, humidity, wind speed, weather condition, city name, and country code.
* `WeatherAnomalyManager` receives the `WeatherSnapshot` and checks it using separate weather interpreter classes.
* Each interpreter checks for one type of weather anomaly:

  * `HumidityAnomalyInterpreter` checks for humid or rainy conditions.
  * `TemperatureAnomalyInterpreter` checks for high-temperature conditions.
  * `StormAnomalyInterpreter` checks for windy, stormy, or thunderstorm conditions.
* If an anomaly is detected, the matching world effect is applied.
* `ConductiveRainEffect` creates or modifies wet terrain such as `Puddle`, suppresses extinguishable hazards, and triggers charge-related behaviour.
* `HeatDistortionEffect` applies heat-based world changes when the API reports high temperature.
* `StormSurgeEffect` applies storm-based consequences such as actor damage, terrain disturbance, and atmospheric charge behaviour.
* The effects avoid overwriting important facility terrain such as the `SuperComputer`.
* Capability checks are used to decide whether a tile can safely be changed.
* The system avoids `switch` statements and `instanceof` checks by using polymorphism, interfaces, and capability-based behaviour.

---

### The Architecture

#### New Abstractions

| Abstraction                 | Type      | Role                                                                                           |
| --------------------------- | --------- | ---------------------------------------------------------------------------------------------- |
| `WeatherAnomalyInterpreter` | Interface | Interprets a `WeatherSnapshot` and decides whether a specific weather anomaly should activate. |
| `AnomalyWorldEffect`        | Interface | Represents a weather effect that changes the game world after an anomaly has been detected.    |

`WeatherAnomalyInterpreter` and `AnomalyWorldEffect` are the two main abstractions for REQ5. They separate weather decision-making from world modification, which makes the system easier to extend and maintain.

---

#### Concrete Classes

| Class                           | Status | Abstraction Implemented     | Complex Effect                                                                                                            |
| ------------------------------- | ------ | --------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `HumidityAnomalyInterpreter`    | New    | `WeatherAnomalyInterpreter` | Checks humidity and weather condition values from `WeatherSnapshot` to determine whether Conductive Rain should activate. |
| `TemperatureAnomalyInterpreter` | New    | `WeatherAnomalyInterpreter` | Checks temperature values from `WeatherSnapshot` to determine whether Heat Distortion should activate.                    |
| `StormAnomalyInterpreter`       | New    | `WeatherAnomalyInterpreter` | Checks wind speed and weather condition values from `WeatherSnapshot` to determine whether Storm Surge should activate.   |
| `ConductiveRainEffect`          | New    | `AnomalyWorldEffect`        | Creates or modifies wet terrain, suppresses extinguishable hazards, and triggers charge-related behaviour.                |
| `HeatDistortionEffect`          | New    | `AnomalyWorldEffect`        | Applies high-temperature world effects and connects hot API weather to environmental hazard behaviour.                    |
| `StormSurgeEffect`              | New    | `AnomalyWorldEffect`        | Applies storm-based world changes such as actor damage, terrain disturbance, and atmospheric charge behaviour.            |

---

### Supporting Classes

| Class                   | Package         | Role                                                                                                                                                   |
| ----------------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `WeatherSyncAction`     | `game.actions`  | Action offered by the `SuperComputer`. It builds the weather query, calls the API client, receives the snapshot, and passes it to the anomaly manager. |
| `WeatherAnomalyManager` | `game.managers` | Coordinates all weather interpreters and world effects using abstraction-based lists.                                                                  |
| `WeatherApiClient`      | `game.weather`  | Sends the HTTP request to OpenWeather and converts the response into a `WeatherSnapshot`.                                                              |
| `WeatherQuery`          | `game.weather`  | Stores query values such as latitude, longitude, API key, and units, then builds the final API URL.                                                    |
| `WeatherSnapshot`       | `game.weather`  | Stores parsed weather values needed by the game.                                                                                                       |
| `WeatherConfig`         | `game.weather`  | Reads the API key from the `OPENWEATHER_API_KEY` environment variable.                                                                                 |
| `WeatherApiException`   | `game.weather`  | Represents API-related errors, such as a missing key, failed request, or parsing problem.                                                              |
| `WeatherSystemFactory`  | `game.weather`  | Builds and connects the weather action, API client, manager, interpreters, and effects.                                                                |
| `WeatherMapAnchor`      | `game.enums`    | Links in-game map context to real-world coordinates used in the API request.                                                                           |
| `FacilityCapability`    | `game.enums`    | Protects important facility terrain such as the `SuperComputer` from being overwritten by weather effects.                                             |

---

### Existing Systems Integrated

| Existing Class/Interface  | Package                   | How REQ5 Uses It                                                                    |
| ------------------------- | ------------------------- | ----------------------------------------------------------------------------------- |
| `SuperComputer`           | `game.grounds`            | Offers the weather sync action to the worker.                                       |
| `Puddle`                  | `game.grounds`            | Created or modified during Conductive Rain.                                         |
| `ChargeReactive`          | `game.highvoltage`        | Used when Conductive Rain interacts with charge-reactive terrain or objects.        |
| `ChargeContext`           | `game.highvoltage`        | Provides context for applying charge-related behaviour during weather effects.      |
| `GalvanicCharge`          | `game.highvoltage`        | Used to represent electrical charge during conductive rain or storm surge.          |
| `AtmosphericChargeSource` | `game.grounds`            | Used during Storm Surge to connect storm weather with atmospheric charge behaviour. |
| `Extinguishable`          | relevant existing package | Suppressed or reduced by Conductive Rain where applicable.                          |
| `FacilityCapability`      | `game.enums`              | Prevents important facility terrain from being replaced by weather-created terrain. |

---

### Marker Note — Six Main Concrete Classes for REQ5

The six main concrete classes that demonstrate the REQ5 abstraction design are:

| # | Class                           | Status | Abstraction Implemented     | Complex Effect                                                                                                   |
| - | ------------------------------- | ------ | --------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| 1 | `HumidityAnomalyInterpreter`    | New    | `WeatherAnomalyInterpreter` | Reads humidity and condition values from `WeatherSnapshot` to determine whether Conductive Rain should activate. |
| 2 | `TemperatureAnomalyInterpreter` | New    | `WeatherAnomalyInterpreter` | Reads temperature from `WeatherSnapshot` to determine whether Heat Distortion should activate.                   |
| 3 | `StormAnomalyInterpreter`       | New    | `WeatherAnomalyInterpreter` | Reads wind speed and condition values from `WeatherSnapshot` to determine whether Storm Surge should activate.   |
| 4 | `ConductiveRainEffect`          | New    | `AnomalyWorldEffect`        | Creates or modifies wet terrain, suppresses extinguishable hazards, and triggers charge-related behaviour.       |
| 5 | `HeatDistortionEffect`          | New    | `AnomalyWorldEffect`        | Applies high-temperature environmental changes based on API weather data.                                        |
| 6 | `StormSurgeEffect`              | New    | `AnomalyWorldEffect`        | Applies storm-based terrain changes, actor damage, and atmospheric charge behaviour.                             |

---

### API Request Design

The OpenWeather request is built dynamically by `WeatherQuery`. This means the request changes based on the current game state instead of always using the same fixed URL.

Example request format:

```text
https://api.openweathermap.org/data/2.5/weather?lat=-27.4705&lon=153.0260&appid=${OPENWEATHER_API_KEY}&units=metric
```

The actual latitude and longitude are determined using the current map context and actor position. This satisfies the REQ5 rule that the API request must be dynamically driven by the current game state.

---

### Why This Design Satisfies REQ5

This design satisfies REQ5 because the system uses an external API in a way that directly affects the game world. The weather data is not only displayed to the player. It is interpreted and used to trigger terrain changes, actor effects, hazard changes, and charge-related behaviours.

The API request is also dynamic because it is built from the current game state. The system uses the actor’s current location and map context to influence the real-world coordinates sent to OpenWeather.

The design includes two clear abstractions:

1. `WeatherAnomalyInterpreter`
2. `AnomalyWorldEffect`

Each abstraction has three concrete implementations, giving the system a clear polymorphic structure. `WeatherAnomalyManager` depends on these abstractions rather than depending directly on the concrete classes. This supports DIP and OCP because new weather types can be added later without rewriting the manager.

Overall, the design follows SRP, OCP, DIP, encapsulation, polymorphism, and secure API handling while still creating meaningful game-state impact.
