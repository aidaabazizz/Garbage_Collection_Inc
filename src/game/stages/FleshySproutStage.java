package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;
import game.managers.Spawner;

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
    private int age = 0;

    /**
     * Constructor for the Sprout stage.
     * @param spawner The spawning manager used to handle Slime creation.
     */
    public FleshySproutStage(Spawner spawner) { super(spawner); }
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
        if (!targets.isEmpty()) {
            display.println(String.format("Fleshy Sprout at %s current age: (%d/%d)",
                    location, age, GROWTH_THRESHOLD));
            display.println("Fleshy Sprout Tree at " + location + " is producing Slime!");
            for (Actor worker : targets) {
                spawner.spawnSlime(location);
            }
            return this;
        }
        // aging/growing (only if not spawning)
        age++;
        display.println(String.format("Fleshy Sprout at %s current age: (%d/%d)",
                location, age, GROWTH_THRESHOLD));
        if (age >= GROWTH_THRESHOLD) {
            // reset the counter here
            // if the 25% fails, we start counting another 20 turns.
            age = 0;

            if (random.nextDouble() <= GROWTH_CHANCE) {
                display.println(String.format(
                        "Fleshy Tree Sprout ('%s') at %s grows into a Fleshy Sapling ('v')!",
                        getDisplayChar(), location));
                return new FleshySaplingStage(spawner);
            } else {
                display.println("Fleshy Sprout at " + location + " failed the 25% growth roll. Resetting counter.");
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
