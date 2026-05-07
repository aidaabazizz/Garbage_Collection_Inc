package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.managers.CreatureSpawner;
import game.managers.Spawner;

import java.util.Random;

/**
 * Abstract implementation of TreeStage providing shared utility methods.
 * Fulfills the DRY principle by centralizing worker detection and dependency management.
 *
 * @author Jewell Gomes
 */
public abstract class AbstractTreeStage implements TreeStage {
    /** Random number generator for growth probabilities. */
    protected final Random random = new Random();
    /** Spawning service used to handle creature creation and REQ4 effects. */
    protected final Spawner spawner = new CreatureSpawner();

    /**
     * Identifies a nearby worker actor in the surrounding eight tiles.
     * @param location The current location of the flora.
     * @return The detected worker Actor, or null if none are adjacent.
     */
    protected Actor getNearbyWorker(Location location) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                return adj.getActor();
            }
        }
        return null;
    }

    /**
     * Finds a valid, empty, and passable location adjacent to the current one.
     * @param location The current location of the tree.
     * @return A valid neighbor Location, or null if all surroundings are blocked.
     */
    protected Location getSpawnLocation(Location location) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            // Check if the tile is empty AND passable (not a wall/locked door)
            if (!adj.containsAnActor() && adj.getGround().canActorEnter(null)) {
                return adj;
            }
        }
        return null;
    }
}
