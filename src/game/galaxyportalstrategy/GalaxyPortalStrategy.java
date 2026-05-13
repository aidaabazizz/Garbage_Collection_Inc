package game.galaxyportalstrategy;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

import java.util.Random;

/**
 * REQ5: Standard Galaxy Portal strategy.
 * Spawns CrazyChicken and Elsa with 50/50 chance.
 * Follows the same pattern as StandardHoleStrategy and ParasiticHoleStrategy.
 *
 * @author Aida
 */
public class GalaxyPortalStrategy implements GalaxyPortalSpawnStrategy {
    private final Random random = new Random();

    @Override
    public boolean spawn(Location location, Spawner spawner) {
        // 50/50 chance between CrazyChicken and Elsa
        if (random.nextBoolean()) {
            return spawner.spawnCrazyChicken(location);
        } else {
            return spawner.spawnElsa(location);
        }
    }
}