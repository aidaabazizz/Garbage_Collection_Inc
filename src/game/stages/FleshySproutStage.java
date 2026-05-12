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
 *
 * @author Jewell Gomes
 */
public class FleshySproutStage extends FleshyTreeStage {
    private static final int GROWTH_THRESHOLD = 20;
    private static final double GROWTH_CHANCE = 0.25;

    /**
     * Constructor for the Sprout stage.
     * @param spawner The spawning manager used to handle Slime creation.
     */
    public FleshySproutStage(Spawner spawner) { super(spawner); }
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
        updateAge(location, GROWTH_THRESHOLD, "Fleshy Sprout");
        List<Actor> targets = SpatialSearch.getNearbyWorkers(location);

        if (!targets.isEmpty()) {
            display.println("Fleshy Sprout Tree at " + location + " is producing Slime!");
            for (Actor worker : targets) {
                spawner.spawnSlime(location);
            }
            return this;
        }

        // aging/growing (only if not spawning)
        if (checkGrowthThreshold(GROWTH_THRESHOLD, GROWTH_CHANCE, location,"Fleshy Sprout")) {
            display.println(String.format(
                    "Fleshy Tree Sprout ('%s') at %s grows into a Fleshy Sapling ('v')!",
                    getDisplayChar(), location));
            return new FleshySaplingStage(spawner);
        }
        return this;
    }

    /**
     * This method returns the lowercase letter y which is the visual
     * representation of this stage on the game map.
     *
     * @return The character 'y'.
     */
    @Override
    public char getDisplayChar(){
        return 'y';
    }
}
