package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;

import java.util.List;

/**
 * This class represents the sprout stage of a mutated fleshy tree.
 * It is responsible for spawning slimes when workers are nearby and
 * eventually growing into a sapling stage. It follows the requirement
 * that each plant can only perform one major action per game turn.
 *
 * @author Jewell Gomes
 */
public class FleshySproutStage extends AbstractTreeStage {
    private static final int GROWTH_THRESHOLD = 20;
    private static final double GROWTH_CHANCE = 0.25;
    private int age = 0;

    /**
     * This method manages the behavior of the sprout during every turn.
     * It first identifies all workers in the surrounding tiles. If a
     * slime is successfully spawned the sprout will not try to grow
     * during that same turn. Otherwise, it increments its age and has
     * a chance to mature into a fleshy sapling stage.
     */
    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        List<Actor> targets = getNearbyWorkers(location);
        boolean spawned = false;
        for (Actor worker : targets) {
            if (spawner.spawnSlime(location)) {
                spawned = true; // At least one slime was spawned
            }
        }
        age++;
        if (!spawned && age >= GROWTH_THRESHOLD) {
            // reset the counter here
            // if the 25% fails, we start counting another 20 turns.
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Fleshy Tree Sprout ('%s') at %s grows into a Fleshy Sapling ('v')!",
                        getDisplayChar(), location));
                return new FleshySaplingStage();
            }
        }
        return this;
    }

    /**
     * This method returns the lowercase letter y which is the visual
     * representation of this stage on the game map.
     */
    @Override
    public char getDisplayChar(){
        return 'y';
    }
}
