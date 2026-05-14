package game.grounds;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.holestrategies.HoleSpawnStrategy;
import game.managers.Spawner;

/**
 * A specialized ground type that acts as a creature spawner.
 * The hole tracks the passage of game turns and attempts to add a new
 * creature (either an Undead or a Slime) to the map every twenty turns
 * if the tile is unoccupied.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake (Modified by)
 */
public class Hole extends Ground {
    /** The number of turns that must pass before a creature is spawned. */
    private static final int SPAWN_INTERVAL = 20;
    /** Counter to track elapsed turns since the last spawn attempt. */
    private int turnCounter = 0;
    /** The strategy defining which creatures the hole is capable of spawning. */
    private final HoleSpawnStrategy strategy;
    /** The spawning manager used to handle environmental triggers and reactions. */
    private final Spawner spawner;
    /** The probability (1%) that the hole will expand to an adjacent tile after a successful spawn. */
    private static final double EXPANSION_CHANCE = 0.01; // 1% (req4)

    /**
     * Constructs a new Hole instance.
     * Initializes the hole with a display character of 'o' and the display name "Hole".
     * The hole begins with a turn counter at zero, counting upward until the
     * spawning threshold is reached.
     */
    public Hole(HoleSpawnStrategy strategy,Spawner spawner) {
        super('o', "Hole");
        this.strategy = strategy;
        this.spawner = spawner;
    }

    /**
     * Updates the spawn counter every turn. Triggers a spawn attempt
     * when the counter reaches the required threshold.
     * @param location The map location of the hole.
     */
    @Override
    public void tick(Location location) {
        turnCounter++;
        if (turnCounter >= SPAWN_INTERVAL) {
            turnCounter = 0;
            //Delegate spawn logic to the strategy (REQ4: dynamic spawning based on map)
            boolean success = strategy.spawn(location, spawner);
            if (success && Math.random() < EXPANSION_CHANCE) {
                // 1% chance to expand
                rollForExpansion(location);
            }
        }
    }

    /**
     * Attempts to expand the hole to one random adjacent tile.
     * Requirement 4: Converts an adjacent passable tile into another Hole that
     * inherits the same spawning capabilities.
     *
     * @param location The current location of the parent hole.
     */
    private void rollForExpansion(Location location) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // Check if it's passable (Dirt/Floor)
            if (adj.getGround().canActorEnter(null)) {
                // Set the ground at the adjacent location to a new Hole with a clone of the strategy
                adj.setGround(new Hole(strategy.cloneStrategy(),spawner));
                return; // Expand only once per successful trigger
            }
        }
    }
}