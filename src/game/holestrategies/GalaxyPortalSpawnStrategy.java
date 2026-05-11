package game.holestrategies;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

/**
 * REQ5: Strategy interface for Galaxy Portal spawning behavior.
 * Follows the same pattern as HoleSpawnStrategy.
 *
 * @author Aida
 */
public interface GalaxyPortalSpawnStrategy {
    boolean spawn(Location location, Spawner spawner);
}