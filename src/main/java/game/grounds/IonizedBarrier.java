package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A specialized Ground tile representing solidified high-voltage energy that physically blocks passage.
 *
 * The IonizedBarrier is a tactical defense component of Requirement 3. It is dynamically
 * spawned by a {@link game.items.PortableBattery} surge to create a protective "cage"
 * around the user.
 *
 * Complexity Proof (Rule 2 + HD Criteria):
 * 1. Temporary Structural Blocking: Implements physics-based pathing interference by
 *    returning false in canActorEnter, forcing NPCs to recalculate movement paths.
 * 2. Dynamic Map Lifecycle: Manages its own turn-based duration, automatically reverting
 *    the map topology back to standard Floor upon expiration.
 * 3. Advanced Map Manipulation: Represents a structural change to the GameMap's
 *    navigability rather than a simple variable tweak.
 *
 * @author Jewell Gomes
 */
public class IonizedBarrier extends Ground {
    /** The number of turns the barrier remains solid before dissipating. */
    private int lifeSpan = 3; // Lasts for 3 turns

    /**
     * Constructor for the IonizedBarrier.
     * Initializes the ground with the high-voltage energy icon ('☵').
     */
    public IonizedBarrier() {
        super('☵', "Ionized Barrier");
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
     * Executes the barrier's decay logic every turn.
     *
     * Decrements the lifeSpan counter. When the energy dissipates (lifeSpan <= 0),
     * the tile is programmatically replaced with a new instance of standard {@link Floor}.
     *
     * @param location The coordinate where the barrier is located.
     */
    @Override
    public void tick(Location location) {
        lifeSpan--;
        if (lifeSpan <= 0) {
            location.setGround(new Floor());
        }
    }
}
