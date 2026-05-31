package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.SanctuaryStatus;
import game.enums.DistortionCapability;
import game.sanctuary.DistortionSource;


public class CorruptedSafeHouse extends Ground implements DistortionSource {

    /** Whether this safe house has been stabilised by a SuperComputer action. */
    private boolean isStabilised = false;

    /**
     * Creates a CorruptedSafeHouse with the CORRUPTED capability tag.
     */
    public CorruptedSafeHouse() {
        super('⌂', "Corrupted Safe House");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    /**
     * Each turn: if an actor stands here, grant SanctuaryStatus and (if not
     * stabilised) spawn BlueFire on safe exits.
     *
     * @param location this tile's location on the map
     */
    @Override
    public void tick(Location location) {
        if (!location.containsAnActor()) {
            return;
        }
        releaseDistortion(location.getActor(), location.map(), location);

        if (!isStabilised) {
            spawnBlueFire(location);
        }
    }

    /**
     * Grants the actor {@link SanctuaryStatus} for 5 turns.
     *
     * @param actor    the actor entering the safe house
     * @param map      the game map
     * @param location this tile's location
     * @return result description
     */
    @Override
    public String releaseDistortion(Actor actor, GameMap map, Location location) {
        actor.addStatus(new SanctuaryStatus(5));
        return actor + " enters the sanctuary — but the air around it ignites!";
    }

    /**
     * Stabilises this safe house: disables CORRUPTED capability and prevents
     * further BlueFire spawning.
     *
     * @param location this tile's location (unused but required by interface)
     * @return result description
     */
    @Override
    public String stabilise(Location location) {
        isStabilised = true;
        this.disableAbility(DistortionCapability.CORRUPTED);
        return "Blue Fire spread has been suppressed around the safe house!";
    }

    /**
     * Sets stabilised state directly (for testing or alternative control flows).
     *
     * @param stabilised the new stabilised state
     */
    public void setStabilised(boolean stabilised) {
        this.isStabilised = stabilised;
    }

    /**
     * Spawns {@link BlueFire} on adjacent exits, guarding against overwriting
     * grounds that should not be replaced (e.g. ToxicWaste, other DistortionSources,
     * existing BlueFire, Walls).
     *
     * <p>FIX: Previously overwrote any ground. Now only replaces walkable,
     * non-hazardous grounds (Floor, Dirt, Puddle) identified by capability absence.
     * This prevents silently destroying ToxicWaste or other DistortionSources.</p>
     *
     * @param location this tile's location
     */
    private void spawnBlueFire(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Ground existing = destination.getGround();

            // Guard 1: Don't overwrite another DistortionSource (portals, rage ground, etc.)
            if (existing.hasAbility(DistortionCapability.CORRUPTED)) {
                continue;
            }

            // Guard 2: Don't overwrite impassable grounds (Walls, Doors)
            if (existing.hasAbility(DistortionCapability.IMPASSABLE)) {
                continue;
            }

            // Guard 3: Don't overwrite existing timed hazards (BlueFire already burning)
            if (existing.hasAbility(DistortionCapability.ACTIVE_HAZARD)) {
                continue;
            }

            destination.setGround(new BlueFire(5,existing));
        }
    }
}