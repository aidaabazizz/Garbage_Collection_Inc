package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.actors.Undead;

import java.util.Random;

/**
 * A specialized ground type that acts as a creature spawner.
 * The hole tracks the passage of game turns and attempts to add a new
 * creature (either an Undead or a Slime) to the map every twenty turns
 * if the tile is unoccupied.
 *
 * @author Jewell Gomes
 */
public class Hole extends Ground {
    /** The number of turns that must pass before a creature is spawned. */
    private static final int SPAWN_INTERVAL = 20;
    private int turnCounter = 0;
    private final Random random = new Random();

    /**
     * Constructs a new Hole instance.
     * Initializes the hole with a display character of 'o' and the display name "Hole".
     * The hole begins with a turn counter at zero, counting upward until the
     * spawning threshold is reached.
     */
    public Hole() {
        super('o', "Hole");
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
            spawnCreature(location);
        }
    }

    /**
     * Randomly selects a moon creature and adds it to the current location
     * if no other actor is occupying the space. If the tile is empty, it randomly selects either an Undead or a Slime with
     * a 50/50 probability and places that creature at the location.
     * @param location The map location where the creature will spawn.
     */
    private void spawnCreature(Location location) {
        if (!location.containsAnActor()) {
            try {
                if (random.nextBoolean()) {
                    location.addActor(new Undead());
                } else {
                    location.addActor(new Slime());
                }
            } catch (Exception ignored) {}
        }
    }
}