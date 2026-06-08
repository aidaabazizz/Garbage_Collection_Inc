package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.*;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.capabilities.KillerInstinctStatus;
import game.managers.QuotaManager;
import game.sanctuary.DistortionSource;
import java.util.Random;

/**
 * A volatile ground type that grants combat bonuses to workers before relocating itself.
 *
 * The Rage Ground acts as a dynamic hazard. When an actor with the WORKER
 * capability steps on it, they are granted KillerInstinctStatus. Immediately after
 * triggering, the ground vanishes from its current location and reappears at a random
 * valid location on the map.
 *
 * It provides high-risk utility: while it buffs workers, its unpredictable relocation
 * and "burst" audit effect can lead to chaotic combat scenarios.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class RageGround extends Ground implements DistortionSource {
    /** Random number generator for relocation logic. */
    private final Random random = new Random();

    /** The number of turns the Killer Instinct status lasts once applied. */
    private static final int KILLER_INSTINCT_SPAN = 3;

    /**
     * Constructor.
     * Initializes the ground with the '╬' symbol and the CORRUPTED capability.
     */
    public RageGround() {
        super('╬', "Rage Ground");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    /**
     * Updates the ground's state each turn.
     *
     * If a worker steps on this tile and does not already possess the  KillerInstinctStatus,
     * the status is applied and the ground immediately triggers its relocate logic.
     *
     * @param location The current location of the Rage Ground.
     */
    @Override
    public void tick(Location location) {
        Display display = new Display();
        // Trigger relocation only when a Worker steps on it
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            // Only trigger if worker doesn't already have the status
            if (!actor.hasStatus(KillerInstinctStatus.class) && actor.hasAbility(Ability.WORKER)) {
                actor.addStatus(new KillerInstinctStatus(KILLER_INSTINCT_SPAN));
                display.println("\u001B[31m>>> " + actor + " triggers the Rage Ground! It will vanish soon...\u001B[0m");
                relocate(location);
            }
        }
    }

    /**
     * Removes the Rage Ground from its current location and spawns a new one at a random
     * valid tile on the map.
     *
     * A valid location must:
     *     Be traversable by actors.
     *     Not currently contain an actor.
     *     Not already be a corrupted ground type.
     *
     * @param currentLocation The current location of this ground instance.
     */
    private void relocate(Location currentLocation) {
        GameMap map = currentLocation.map();
        currentLocation.setGround(new Floor()); // Disappear
        System.out.println("\u001B[31m>>> The Rage Ground has vanished and relocated!\u001B[0m");

        int x, y;
        Location newLoc;
        do {
            x = random.nextInt(map.getXRange().max());
            y = random.nextInt(map.getYRange().max());
            newLoc = map.at(x, y);
            // Ensure we don't spawn under an actor (prevents infinite loops)
        } while (!newLoc.getGround().canActorEnter(null) ||
                newLoc.containsAnActor() ||
                newLoc.getGround().hasAbility(DistortionCapability.CORRUPTED));

        newLoc.setGround(new RageGround());
    }

    /**
     * Neutralizes the Rage Ground, turning it back into a standard Floor.
     *
     * If a worker is currently standing on the tile, their KillerInstinctStatus
     * is removed immediately to suppress the rage.
     *
     * @param location The location of the ground to be stabilized.
     * @return A message indicating the neutralization of the ground.
     */
    @Override
    public void stabilise(Location location) {
        location.setGround(new Floor());

        // Remove KillerInstinctStatus from any worker on this tile
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(Ability.WORKER)) {
                for (Status status : actor.statuses()) {
                    if (status.getClass() == KillerInstinctStatus.class) {
                        actor.removeStatus(status);
                        break;
                    }
                }
            }
        }

    }

    /**
     * Distortion Audit Protocol: The rage ground discharges all its aggression in
     * one final burst — granting KillerInstinct to ALL actors within radius 1,
     * then immediately relocating (its natural lifecycle but triggered early).
     *
     * @param quotaManager the shared quota system
     * @param location     this tile's location
     * @return audit result description
     */
    @Override
    public String audit(QuotaManager quotaManager, Location location) {
        // 1. Contribute credits to REQ1
        String creditMsg = quotaManager.addCompanyCredits(25);

        // 2. Final unstable effect — grant KillerInstinct to all adjacent actors
        StringBuilder effectMsg = new StringBuilder();
        effectMsg.append("The Rage Ground discharges in a violent burst!\n");

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor()) {
                Actor target = adj.getActor();
                target.addStatus(new KillerInstinctStatus(KILLER_INSTINCT_SPAN));
                effectMsg.append(target).append(" is consumed by rage energy!\n");
            }
        }

        // 3. Trigger immediate relocation (its natural "weakened" state is gone)
        relocate(location);
        effectMsg.append("The Rage Ground's energy is spent — it has relocated!");

        return creditMsg + "\n" + effectMsg;
    }

}