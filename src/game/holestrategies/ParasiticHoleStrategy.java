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
 * @author Aida
 */
public class ParasiticHoleStrategy implements HoleSpawnStrategy {
    /** Random number generator used to decide between spawning targets. */
    private final Random random = new Random();

    /**
     * Executes the spawn logic for the 20-overflow moon.
     * There is a 33% chance to spawn Undead, Parasite, and ScrapSnatcher respectively.
     *
     * @param location The map location where the spawn attempt occurs.
     * @param spawner  The spawning service used to handle creation and environmental side effects.
     * @return true if a creature was successfully added to the map; false otherwise.
     */
    @Override
    public boolean spawn(Location location, Spawner spawner) {
        // Generate a random integer from 0 to 2 (inclusive) for 33% distribution
        int roll = random.nextInt(3);

        // Spawns Undead, Parasite, or ScrapSnatcher with equal probability
        if (roll == 0) {
            return spawner.spawnUndead(location);

        } else if (roll == 1) {
            return spawner.spawnParasite(location);

        } else {
            return spawner.spawnScrapSnatcher(location);
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