package game.holestrategies;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

import java.util.Random;

/**
 * A specific spawning strategy designed for holes located in the 99-deprecated moon.
 * This class implements the standard spawning behavior required for Requirement 4,
 * where holes on the primary moon facility produce a mix of Undead and Slimes.
 * It facilitates the Strategy Pattern by isolating the moon-specific creature
 * pool from the general Hole logic.
 *
 * @author Chathya Attanayake
 * @author Aida (modified by)
 */
public class StandardHoleStrategy implements HoleSpawnStrategy {
    /** Random number generator used to determine the next spawn result. */
    private final Random random = new Random();

    /**
     * Executes the spawn logic for the 99-deprecated moon facility.
     * Requirement 4: There is a 50% chance to spawn an Undead and a 50% chance
     * to spawn a Slime.
     * NEW A3 REQ2: 33% chance for Undead, 33% for Slime, 33% for ScrapSnatcher.
     * @param location The map location where the creature will be created.
     * @param spawner  The spawning service used to handle creation and environmental side effects.
     * @return true if a creature was successfully added to the map; false otherwise.
     */
    @Override
    public boolean spawn(Location location, Spawner spawner) {
        // Generate a random integer from 0 to 2 (inclusive) for 33% distribution
        int roll = random.nextInt(3);

        // Spawns Undead, Slime, or ScrapSnatcher with equal probability
        if (roll == 0) {
            return spawner.spawnUndead(location);

        } else if (roll == 1) {
            return spawner.spawnSlime(location);

        } else {
            return spawner.spawnScrapSnatcher(location);
        }

    }

    /**
     * Creates a new instance of this strategy.
     * This method supports the Requirement 4 expansion logic, allowing new
     * adjacent holes on the moon facility to inherit the Standard spawning profile.
     *
     * @return A new instance of StandardHoleStrategy.
     */
    @Override
    public HoleSpawnStrategy cloneStrategy() { return new StandardHoleStrategy(); }
}