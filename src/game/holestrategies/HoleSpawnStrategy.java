package game.holestrategies;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

/**
 * A strategy interface that defines the spawning behavior for different types of holes.
 * This interface follows the Strategy Pattern, allowing holes on different maps to
 * produce different sets of creatures (e.g., Slimes on moon 99 versus Parasites
 * on moon 20).
 * It also facilitates Requirement 4's expansion logic by allowing the spawning logic
 * to be cloned to new hole instances.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public interface HoleSpawnStrategy {
    /**
     * Executes the specific spawning logic for a hole at the given location.
     * The implementation determines which creatures are eligible to be created.
     *
     * @param location The map location where the creature should be spawned.
     * @param spawner  The spawner manager used to handle creation and environmental triggers.
     * @return true if a creature was successfully added to the map; false otherwise.
     */
    boolean spawn(Location location, Spawner spawner);
    /**
     * Creates a copy of the current spawning strategy.
     * This is used for Requirement 4 expansion logic, ensuring that when a hole
     * gets bigger, the newly created adjacent hole inherits the exact same
     * spawning capabilities as the parent hole.
     *
     * @return A new instance of the same HoleSpawnStrategy implementation.
     */
    HoleSpawnStrategy cloneStrategy();
}
