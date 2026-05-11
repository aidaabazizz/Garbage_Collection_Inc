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
    private int turnCounter = 0;
    private final HoleSpawnStrategy strategy;
    private final Spawner spawner;
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
            // The strategy decides WHAT to spawn
            // The spawner handles the REQ4 environmental reactions
            boolean success = strategy.spawn(location, spawner);

            if (success && Math.random() < EXPANSION_CHANCE) {
                // 1% chance to expand
                rollForExpansion(location);
            }
        }
    }

    private void rollForExpansion(Location location) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // We check if it's passable (Dirt/Floor) so we don't destroy Walls
            if (adj.getGround().canActorEnter(null)) {
                // Transform it! If it's already a hole, it just overwrites itself.
                adj.setGround(new Hole(strategy,spawner));
                return; // Stop after expanding once
            }
        }
    }
}