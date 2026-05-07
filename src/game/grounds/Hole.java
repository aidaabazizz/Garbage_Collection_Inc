package game.grounds;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.HoleMarker;
import game.managers.CreatureSpawner;
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
public class Hole extends Ground implements HoleMarker {
    /** The number of turns that must pass before a creature is spawned. */
    private static final int SPAWN_INTERVAL = 20;
    private int turnCounter = 0;
    private final HoleSpawnStrategy strategy;
    private final Spawner spawner = new CreatureSpawner();
    //private final Random random = new Random();
    private static final double EXPANSION_CHANCE = 0.01; // 1% (req4)

    /**
     * Constructs a new Hole instance.
     * Initializes the hole with a display character of 'o' and the display name "Hole".
     * The hole begins with a turn counter at zero, counting upward until the
     * spawning threshold is reached.
     */
    public Hole(HoleSpawnStrategy strategy) {
        super('o', "Hole");
        this.strategy = strategy;
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

            if (!location.containsAnActor()) {
                // The strategy decides WHAT to spawn
                // The spawner handles the REQ4 environmental reactions
                strategy.spawn(location, spawner);

                // 1% chance to expand
                rollForExpansion(location);
            }
        }
    }

    private void rollForExpansion(Location location) {
        if (Math.random() < EXPANSION_CHANCE) {
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();

                // Use getGroundAs with the Marker Interface
                if (adj.getGroundAs(HoleMarker.class) == null) {
                    // Inherit the exact same strategy (capability)
                    adj.setGround(new Hole(this.strategy));
                    return;
                }
            }
        }
    }
}