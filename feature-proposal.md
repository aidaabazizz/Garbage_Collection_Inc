# Feature Proposal — REQ3 & REQ4: Creative Mode

---

## REQ3: High-Voltage Galvanic System

### The Pitch

The High-Voltage Galvanic System introduces a persistent, cross-component electrical ecosystem
to the moon facility. Conductive energy sources (Emitters) release high-voltage surges that
cascade through terrain, actors, and items simultaneously. This system transforms the environment
dynamically — electrifying puddles into hazards, evolving dormant creatures into predators,
powering magnetic tools to harvest scrap remotely, and paralysing workers caught in the current.

### The Mechanics

- **Emitters** (AtmosphericChargeSource, TeslaCoil, PortableBattery) release `GalvanicCharge`
  pulses into the map via `ChargeSource.releaseCharge(location, charge)`.
- **AtmosphericChargeSource** fires a random lightning strike anywhere on the map each turn
  with a 10% probability.
- **TeslaCoil** charges over 3 turns and auto-discharges a diamond-shaped (Manhattan Distance ≤ 2)
  AoE blast; it can also be manually triggered by an adjacent actor, or overloaded by an
  incoming charge from another source.
- **PortableBattery** is a single-use item carried in inventory; on activation it permanently
  converts the ground beneath the user to a `PoweredFloor`, spawns `IonizedBarrier` walls on
  all 8 surrounding tiles (blocking NPC pathfinding), and zaps every actor in the radius.
- **Resonators** (Puddle, ElectrifiedPuddle, PoweredFloor, IonizedBarrier, DormantStaticCreature,
  Wallet) implement `ChargeReactive.reactToCharge(location, charge)` to define their response:
  - `Puddle` → Performs a multi-stage reaction. It uses `ChargeUtils` to zap occupants immediately upon impact before undergoing a structural map change into an `ElectrifiedPuddle`.
  - `ElectrifiedPuddle` → stacks lifespan (up to 3× base), continuously damages occupants,
    and arcs `ParalyzedStatus` (20% chance) to all 8 adjacent actors each turn.
  - `PoweredFloor` → propagates the charge to all 8 neighbours and zaps their occupants.
  - `IonizedBarrier` → blocks actor passage, magnetically locks all `MAGNETIC` items in a 3×3
    area, and reinforces itself if struck again (+3 turns per hit).
  - `DormantStaticCreature` → irreversibly evolves into a `StaticStalker` upon receiving a charge.
  - `Wallet` → activates as an electromagnet, pulling all `MAGNETIC` items from a 5×5 grid
    (subject to wall-based ray-cast flux blocking) into the holder's inventory or to their feet.
- **StaticStalker** (evolved predator) emits a passive "Static Aura" every turn:
  heals itself when standing on `ENERGIZED` terrain, morphs adjacent Puddles, applies
  `ParalyzedStatus` (20%, or 40% if overcharged) to neighbouring actors, and magnetically
  triggers `ChargeReactive` items in adjacent inventories.
- **ParalyzedStatus** forces the affected actor to skip their turn and simultaneously grants
  `MaterialCapability.REFLECTIVE`, causing any attacker to suffer reflective damage and
  receive `ShockedStatus` (electrical arc-back via `AttackAction`).
- **ShockedStatus** extends `DamageOverTimeStatus`; while active it marks the host as
  `CONDUCTIVE` and releases a 1-tile AoE pulse every turn, triggering ground and actor
  reactions on all 8 neighbours (the "Human Lightning Bolt" effect).
- **GalvanicCharge** carries a visited `HashSet<Location>` to prevent infinite recursion
  between adjacent conductive tiles and ensure each entity is zapped at most once per surge.
- **MaterialCapability** enum (`ENERGIZED`, `MAGNETIC`, `MAGNETICALLY_LOCKED`, `CONDUCTIVE`,
  `PARALYZED`, `REFLECTIVE`) replaces all `instanceof` checks with a capability-tag system,
  strictly following the Dependency Inversion Principle.
- **MagneticItem** (abstract base for CRTMonitor, FloppyDisk, Lantern) automatically grants
  `MAGNETIC`; overrides `allowableActions(Location)` to return an empty list while
  `MAGNETICALLY_LOCKED` is active, disabling manual pick-up.

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

| Class | Status | Abstraction Implemented/Extended | Complex Effect                                                                                      |
|---|---|---|-----------------------------------------------------------------------------------------------------|
| `GalvanicCharge` | **New** | `ChargeContext` | Carries surge metadata + visited-set memory for recursion prevention                                |
| `TeslaCoil` | **New** | `ChargeSource`, `ChargeReactive` | Temporal capacitor cycle, diamond AoE, Overload chain reactions                                     |
| `AtmosphericChargeSource` | **New** | `ChargeSource` | Random remote map-wide lightning strikes every turn                                                 |
| `PortableBattery` | **New** | `ChargeSource` | Terrain morphing + IonizedBarrier spawning + AoE impact                                             |
| `DormantStaticCreature` | **New** | `ChargeReactive` | Permanent actor replacement (evolution) upon charge receipt                                         |
| `StaticStalker` | **New** | `NonPlayerCharacter` (extended) | Static Aura: ground synergy, terrain morphing, proximity stun, inventory induction                  |
| `Puddle` | **Retrofitted** (A1/A2) | `ChargeReactive` | Conducts immediate damage to occupants + Structural terrain morphing → ElectrifiedPuddle |
| `ElectrifiedPuddle` | **New** | `ChargeReactive` | Timed hazard, lifespan stacking, AoE paralysis arcing                                               |
| `PoweredFloor` | **New** | `ChargeReactive` | Cascade propagation to 8 neighbours                                                                 |
| `IonizedBarrier` | **New** | `ChargeReactive` | Pathing blockage + magnetic locking of nearby items                                                 |
| `Wallet` | **Retrofitted** (A1/A2) | `ChargeReactive` | 5×5 AoE magnetic item harvesting with wall-based ray-cast flux blocking                             |
| `MagneticItem` | **New** | Abstract class | Base for all magnetic scrap; disables pick-up when `MAGNETICALLY_LOCKED`                            |
| `CRTMonitor` | **Retrofitted** (A1/A2) | `MagneticItem` | MAGNETIC capability + fire-spawning short-circuit on sale                                           |
| `FloppyDisk` | **Retrofitted** (A1/A2) | `MagneticItem` | MAGNETIC capability + random credit-deduction glitch on sale                                        |
| `Lantern` | **Retrofitted** (A1/A2) | `MagneticItem` | MAGNETIC capability + oil-drain infection + fire-spread on sale                                     |

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
> | # | Class | Abstraction | Complex Effect                                                                                           |
> |---|---|---|----------------------------------------------------------------------------------------------------------|
> | 1 | `AtmosphericChargeSource` | `ChargeSource` | Random map-wide lightning strikes (10% chance, any coordinate)                                           |
> | 2 | `TeslaCoil` | `ChargeSource` + `ChargeReactive` | 3-turn capacitor + diamond-shaped AoE (Manhattan distance) + overload chain                              |
> | 3 | `PortableBattery` | `ChargeSource` | Single-use: center tile → PoweredFloor, 8 neighbors → IonizedBarrier, AoE zap                            |
> | 4 | `DormantStaticCreature` | `ChargeReactive` | Actor metamorphosis → StaticStalker (behavior tree + static aura)                                        |
> | 5 | `Puddle` | `ChargeReactive` | Conducts immedidate damage to occupants + Ground morphing → ElectrifiedPuddle (timed hazard with arcing) |
> | 6 | `Wallet` | `ChargeReactive` | 5×5 magnetic harvest with wall-based ray-cast flux blocking                                              |
>
> *Please focus your assessment on these six classes when evaluating REQ3.*
---
