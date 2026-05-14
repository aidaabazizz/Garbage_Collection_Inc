package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.PoisonStatus;
import game.managers.Spawner;
import game.utils.SpatialSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A motion-activated biological spawner found within the facility.
 * Unlike standard holes, Vents only trigger when a worker is detected in the
 * immediate surrounding tiles. As per Requirement 4, a successful spawn from a
 * vent results in a toxic cloud that poisons the newly created creature and
 * one random adjacent actor for five turns.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class Vent extends Ground {
    /** Random number generator for spawning decisions and target selection. */
    private final Random rand = new Random();
    /** The number of turns the poison status effect persists. */
    private static final int POISON_DURATION = 5;
    /** The spawning service used to handle creature creation and side effects. */
    private final Spawner spawner;

    /**
     * Constructs a new Vent instance with a reference to the global spawner.
     *
     * @param spawner The spawner manager used to handle Parasite and Slime creation.
     */
    public Vent(Spawner spawner) {
        super('V', "Vent");
        this.spawner = spawner;
    }

    /**
     * Updates the vent's state every turn.
     * The vent triggers a spawn attempt if at least one worker is adjacent and the
     * vent tile itself is unoccupied.
     *
     * @param location The map location of the vent.
     */
    @Override
    public void tick(Location location) {
        // Motion activated trigger (checks for nearby actors with WORKER ability)
        if (!SpatialSearch.getNearbyWorkers(location).isEmpty() && !location.containsAnActor()) {
            // 50/50 chance to spawn either a Parasite or a Slime
            if (rand.nextBoolean()) spawner.spawnParasite(location);
            else spawner.spawnSlime(location);
            // Apply toxic side effects to the surroundings
            applyPoison(location);
        }
    }

    /**
     * Applies a poison status to the newly spawned creature and one random adjacent actor.
     *  Successful spawning triggers a toxic reaction that inflicts 1 damage per turn for 5 turns.
     *
     * @param location The location of the vent where the spawn occurred.
     */
    private void applyPoison(Location location) {
        // 1. Poison the newly spawned creature occupying the vent tile
        if (location.containsAnActor()) location.getActor().addStatus(new PoisonStatus(POISON_DURATION));

        // 2. REQ4 Clarification: Poison exactly one random adjacent actor
        List<Actor> adjacentActors = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().containsAnActor()) {
                adjacentActors.add(exit.getDestination().getActor());
            }
        }
        // Select and poison one random target from the collected adjacent actors
        if (!adjacentActors.isEmpty()) {
            adjacentActors.get(rand.nextInt(adjacentActors.size())).addStatus(new PoisonStatus(POISON_DURATION));
        }
    }
}