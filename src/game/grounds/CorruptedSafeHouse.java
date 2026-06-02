package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.SanctuaryStatus;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.sanctuary.DamageInterceptor;
import game.sanctuary.DistortionSource;

/**
 * A safe house that protects workers but creates a ring of BlueFire around it.
 *
 * Lifecycle (per spec):
 * - Worker enters    → fresh SanctuaryStatus added; BlueFire spawns for 5 turns
 * - Worker stays 1-5 → BlueFire re-spawns each turn; damage halved; 30% heal
 * - Worker stays 6+  → BlueFire stops (isProtectionActive() false); healing continues
 * - Worker leaves    → SanctuaryStatus expires in tickStatus(); BlueFire stops next tick
 * - Worker re-enters → old status is inactive, new SanctuaryStatus added (full reset)
 */
public class CorruptedSafeHouse extends Ground implements DistortionSource {

    private boolean isStabilised = false;

    public CorruptedSafeHouse() {
        super('⌂', "Corrupted Safe House");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    @Override
    public void tick(Location location) {
        if (!location.containsAnActor() || !location.getActor().hasAbility(Ability.WORKER)) {
            return;
        }

        Actor actor = location.getActor();

        // Only grant sanctuary if not stabilised
        if (!isStabilised) {
            if (!actor.hasStatus(SanctuaryStatus.class)) {
                actor.addStatus(new SanctuaryStatus());
                actor.enableAbility(DamageInterceptor.PROTECTED);
                if (Math.random() <= 0.30) {
                    actor.heal(1);
                    System.out.println(">>> " + actor + " is healed by the sanctuary energy.");
                }
            }

            SanctuaryStatus status = actor.statusesOf(SanctuaryStatus.class).get(0);
            if (status.isProtectionActive()) {
                spawnBlueFire(location);
            }
        }
        // If stabilised: no new status, no BlueFire, but existing status continues ticking naturally
    }
    /**
     * Spawns BlueFire on adjacent exits.
     * Guards: won't overwrite DistortionSources, impassable grounds, or existing hazards.
     */
    private void spawnBlueFire(Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            Ground existing = dest.getGround();

            // Skip DistortionSources (portals, rage ground, this safe house)
            if (existing.hasAbility(DistortionCapability.CORRUPTED)) {
                continue;
            }

            // Skip impassable grounds (walls, doors)
            if (!existing.canActorEnter(null)) continue;

            // Skip existing hazards (already burning)
            if (existing.hasAbility(DistortionCapability.ACTIVE_HAZARD)) {
                continue;
            }

            dest.setGround(new BlueFire(3, existing));
        }
    }

    @Override
    public String releaseDistortion(Actor actor, GameMap map, Location location) {
        return actor + " enters the sanctuary — but the air around it ignites!";
    }

    @Override
    public String stabilise(Location location) {
        isStabilised = true;
        this.disableAbility(DistortionCapability.CORRUPTED);
        return "Blue Fire spread has been suppressed!";
    }

//
}