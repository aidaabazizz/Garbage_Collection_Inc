// game/grounds/GalaxyPortal.java
package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.CreatureSpawner;
import game.holestrategies.GalaxyPortalSpawnStrategy;
import game.holestrategies.GalaxyPortalStrategy;

/**
 * REQ5: A mystical portal that spawns CrazyChicken and Elsa.
 * Follows the same pattern as Hole class.
 *
 * @author Aida
 */
public class GalaxyPortal extends Ground {
    /** The number of turns that must pass before a creature is spawned. */
    private static final int SPAWN_INTERVAL = 20;
    private int turnCounter = 0;
    private final GalaxyPortalSpawnStrategy strategy;
    private final CreatureSpawner spawner;

    /**
     * Constructs a new GalaxyPortal instance with default strategy.
     * Initializes the portal with a display character of 'P' and display name "Galaxy Portal".
     */
    public GalaxyPortal() {
        this(new GalaxyPortalStrategy());
    }

    /**
     * Constructs a new GalaxyPortal instance with custom strategy.
     *
     * @param strategy The spawning strategy to use
     */
    public GalaxyPortal(GalaxyPortalSpawnStrategy strategy) {
        super('P', "Galaxy Portal");
        this.strategy = strategy;
        this.spawner = new CreatureSpawner();
    }

    /**
     * Updates the spawn counter every turn. Triggers a spawn attempt
     * when the counter reaches the required threshold.
     *
     * @param location The map location of the portal.
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
     * Attempts to spawn a creature using the configured strategy
     * if no other actor is occupying the space.
     *
     * @param location The map location where the creature will spawn.
     */
    private void spawnCreature(Location location) {
        if (!location.containsAnActor()) {
            try {
                strategy.spawn(location, spawner);
            } catch (Exception ignored) {}
        }
    }
}