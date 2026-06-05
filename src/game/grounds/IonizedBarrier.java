package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeContext;
import game.highvoltage.ChargeReactive;
import game.enums.MaterialCapability;

/**
 * A specialized Ground tile representing solidified high-voltage energy that physically blocks passage.
 *
 * The IonizedBarrier is a tactical defense component of the High-Voltage Galvanic System (Requirement 3).
 * It is dynamically spawned by high-energy surges (e.g., Portable Battery) to create a protective
 * "cage" around a coordinate.
 *
 * Complexity Proof (Requirement 3):
 * 1. Temporary Structural Blocking: Implements physics-based pathing interference, forcing
 *    NPCs to recalculate movement paths by making tiles impassable.
 * 2. Magnetic Interference: While active, the barrier projects an induction field that
 *    "locks" magnetic items on and around it, preventing them from being harvested.
 * 3. Dynamic Map Lifecycle: Manages its own turn-based duration, programmatically
 *    reverting the map topology back to standard Floor upon expiration.
 *
 * @author Jewell Gomes
 */
public class IonizedBarrier extends Ground implements ChargeReactive {
    /** The number of turns the barrier remains solid before dissipating. */
    private int lifeSpan = INITIAL_LIFESPAN; // Lasts for 3 turns
    private static final int INITIAL_LIFESPAN = 3;
    private final Ground previousGround;

    /**
     * Constructor for the IonizedBarrier.
     *
     * @param previousGround The ground instance to be restored when this barrier expires.
     */
    public IonizedBarrier(Ground previousGround) {
        super('☵', "Ionized Barrier");
        this.previousGround = previousGround;
    }

    /**
     * Physics override logic. This method creates the "Blocking" effect by preventing
     * any Actor from entering the tile coordinate.
     *
     * @param actor The actor attempting to enter the tile.
     * @return false, indicating the ground is impassable.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Executes the barrier's decay and locking logic every turn.
     *
     * The method performs the following:
     * 1. Decrements the internal lifespan counter.
     * 2. If the barrier is still active, it ensures all magnetic items on the
     *    current tile and adjacent tiles are {@code MAGNETICALLY_LOCKED}.
     * 3. Upon expiration (lifespan <= 0), it removes the locks from nearby items
     *    and replaces itself with a new instance of {@link Floor}.
     *
     * @param location The coordinate where the barrier is located.
     */
    @Override
    public void tick(Location location) {
        lifeSpan--;
        if (lifeSpan <= 0) {
            updateNearbyLockState(location, false);
            location.setGround(previousGround);
            return;
        }

        updateNearbyLockState(location, true);
    }

    /**
     * Implementation of the ChargeReactive interface to allow barrier reinforcement.
     *
     * If hit by a new galvanic surge, the barrier's structural integrity is sustained
     * by stacking the incoming charge onto its current lifespan.
     *
     * @param location The coordinate of the barrier.
     * @param charge   The ChargeContext representing the reinforcing surge.
     */
    @Override
    public void reactToCharge(Location location, ChargeContext charge) {
        // Add 3 more turns to the barrier.
        this.lifeSpan += INITIAL_LIFESPAN;
        charge.getDisplay().println(" The " + charge.getSourceName() +
                " reinforces the Ionized Barrier! (Intensity: " + this.lifeSpan + ")");
    }

    /**
     * Helper method to manage magnetic interference in a 3x3 area.
     * Iterates through the barrier's tile and its 8 neighbors to apply or
     * remove the {@code MAGNETICALLY_LOCKED} capability.
     *
     * @param center     The location of the barrier.
     * @param shouldLock true to apply the lock; false to release it.
     */
    private void updateNearbyLockState(Location center, boolean shouldLock) {
        // Handle current tile
        setLockAt(center, shouldLock);

        // Handle neighbors
        for (Exit exit : center.getExits()) {
            setLockAt(exit.getDestination(), shouldLock);
        }
    }

    /**
     * Sets or removes the magnetic lock on items at a specific location.
     * Only items with the {@link MaterialCapability#MAGNETIC} capability are affected.
     *
     * @param loc        The location to check for items.
     * @param shouldLock true to enable MAGNETICALLY_LOCKED; false to disable.
     */
    private void setLockAt(Location loc, boolean shouldLock) {
        for (Item item : loc.getItems()) {
            if (item.hasAbility(MaterialCapability.MAGNETIC)) {
                if (shouldLock) {
                    item.enableAbility(MaterialCapability.MAGNETICALLY_LOCKED);
                } else {
                    item.disableAbility(MaterialCapability.MAGNETICALLY_LOCKED);
                }
            }
        }
    }
}
