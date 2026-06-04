package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;
import game.utils.SpatialSearch;

import java.util.List;
import java.util.Random;

/**
 * Sprout stage for Fleshy Tree on 99-deprecated map.
 * Spawns Undead instead of Slime when workers are nearby.
 *
 * Every 20 turns, has 25% chance to grow directly to Mature stage.
 * (No Sapling stage on this map)
 *
 * @author Aida
 */
public class FleshySproutStage99 implements TreeStage {
    private static final int GROWTH_THRESHOLD = 20;
    private static final double GROWTH_CHANCE = 0.25;

    private final Spawner spawner;
    private final Display display;
    private final Random random;
    private int age = 0;

    public FleshySproutStage99(Spawner spawner) {
        this.spawner = spawner;
        this.display = new Display();
        this.random = new Random();
    }

    @Override
    public TreeStage execute(Location location) {
        // Internal state update happens every turn (age increments regardless)
        age++;
        display.println("Fleshy Sprout at " + location + " current age: (" + age + "/" + GROWTH_THRESHOLD + ")");

        // Check for adjacent workers using SpatialSearch (NO instanceof!)
        List<Actor> targets = SpatialSearch.getNearbyWorkers(location);

        // Priority 1: Spawn Undead if workers are adjacent
        if (!targets.isEmpty()) {
            display.println("Fleshy Sprout at " + location + " is producing an Undead!");
            for (Actor worker : targets) {
                spawner.spawnUndead(location);
            }
            return this;  // No growth this turn (single action per turn)
        }

        // Priority 2: Attempt to grow if no spawning occurred
        if (age >= GROWTH_THRESHOLD && random.nextDouble() <= GROWTH_CHANCE) {
            age = 0;
            display.println("Fleshy Sprout at " + location + " grows into a Fleshy Mature Tree!");
            return new FleshyMatureStage99(spawner);
        }

        return this;
    }

    @Override
    public char getDisplayChar() {
        return 'y';
    }
}