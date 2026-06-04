package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;
import game.utils.SpatialSearch;

import java.util.List;

/**
 * This class represents the sprout stage of a mutated fleshy tree.
 * It is responsible for spawning slimes when workers are nearby and
 * eventually growing into a sapling stage. It follows the requirement
 * that each plant can only perform one major action per game turn.
 * Based on A2 feedback, this class has been refactored to be configuration-driven via
 * Dependency Injection. It no longer hardcodes its own growth parameters or its successor stage,
 * allowing for a more flexible and decoupled tree hierarchy.
 * @author Jewell Gomes
 */
public class FleshySproutStage extends FleshyTreeStage {
    /**
     * Constructor for the FleshySproutStage.
     * Uses Dependency Injection to initialize growth parameters, ensuring the class
     * remains a pure logic component independent of specific game-balancing numbers.
     *
     * @param spawner   The spawning manager used to handle Slime creation.
     * @param name      The display name for this stage (e.g., "Fleshy Sprout").
     * @param threshold The temporal threshold required for growth.
     * @param chance    The success rate of the growth roll.
     */
    public FleshySproutStage(Spawner spawner, String name, char displayChar, int threshold, double chance) {
        super(spawner, name, displayChar, threshold, chance);
    }
    /**
     * This method manages the behavior of the sprout during every turn.
     * It first identifies all workers in the surrounding tiles. If a
     * slime is successfully spawned the sprout will not try to grow
     * during that same turn. It will always increment the age as per ed discussion
     * age incrementing is an internal state not an action. It will prioritize spawning over
     * growing into a mature fleshy sapling.
     *
     * @param location  The current map location of the tree.
     */
    @Override
    public TreeStage execute(Location location) {
        // Ed discussion: Age increases every turn no matter what.
        updateAge(location);
        List<Actor> targets = SpatialSearch.getNearbyWorkers(location);

        if (!targets.isEmpty()) {
            display.println(name + " at " + location + " is producing Slime!");
            for (Actor worker : targets) {
                spawner.spawnSlime(location);
            }
            return this;
        }

        // growing (only if not spawning)
        if (checkGrowthThreshold(location) && nextStage != null) {
            display.println(String.format(
                    "%s at %s grows into %s!",
                    name, location, nextStage));
            return this.nextStage; // based on the A2 feedback, reactor the hardcoded transition to use dependency injection
            // returning the pre-injected nextStage instead of instantiating a specific class
        }
        return this;
    }
}
