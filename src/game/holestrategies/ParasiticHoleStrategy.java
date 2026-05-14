package game.holestrategies;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

import java.util.Random;

/**
 * A specific spawning strategy designed for holes located in the 20-overflow factory moon.
 * This class implements Requirement 4, where holes on this moon produce a mix of
 * Undead and Parasites.
 * It provides a deterministic way to handle creature generation while allowing for cloning when the hole expands.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class ParasiticHoleStrategy implements HoleSpawnStrategy {
    /** Random number generator used to decide between spawning targets. */
    private final Random random = new Random();

    /**
     * Executes the spawn logic for the 20-overflow moon.
     * There is a 50% chance to spawn an Undead and a 50% chance to spawn a Parasite.
     *
     * @param location The map location where the spawn attempt occurs.
     * @param spawner  The spawning service used to handle creation and environmental side effects.
     * @return true if a creature was successfully added to the map; false otherwise.
     */
    @Override
    public boolean spawn(Location location, Spawner spawner) {
        // Spawns Undead and Parasites
        if (random.nextBoolean()) {
            return spawner.spawnUndead(location);
        } else {
            return spawner.spawnParasite(location);
        }
    }

    /**
     * Creates a new instance of this strategy.
     * This is required for Requirement 4's hole expansion logic, ensuring new holes
     * on Moon 20 maintain the same spawning profile.
     *
     * @return A new instance of ParasiticHoleStrategy.
     */
    @Override
    public HoleSpawnStrategy cloneStrategy() { return new ParasiticHoleStrategy(); }
}