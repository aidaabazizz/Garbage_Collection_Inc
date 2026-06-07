package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;
import game.utils.SpatialSearch;

import java.util.List;
import java.util.Random;

/**
 * Mature stage for Fleshy Tree on 99-deprecated map.
 * Spawns ScrapSnatcher instead of Undead when workers are nearby.
 *
 * Every 35 turns, has 50% chance to grow into FleshyMonolith.
 *
 * @author Aida
 */
public class FleshyMatureStage99 implements TreeStage {
    private static final int MONOLITH_THRESHOLD = 35;
    private static final double MONOLITH_CHANCE = 0.50;

    private final Spawner spawner;
    private final Display display;
    private final Random random;
    private int age = 0;

    public FleshyMatureStage99(Spawner spawner) {
        this.spawner = spawner;
        this.display = new Display();
        this.random = new Random();
    }

    @Override
    public TreeStage execute(Location location) {
        // ADD THIS NULL CHECK
        if (location == null) {
            return this;
        }

        age++;

        // Check for adjacent workers using SpatialSearch
        List<Actor> targets = SpatialSearch.getNearbyWorkers(location);

        // Spawn ScrapSnatcher if workers are adjacent
        if (!targets.isEmpty()) {
            display.println("Fleshy Mature Tree at " + location + " is producing a Scrap Snatcher!");
            for (Actor worker : targets) {
                spawner.spawnScrapSnatcher(location);
            }
        }

        // Check for growth to Monolith
        if (age >= MONOLITH_THRESHOLD && random.nextDouble() <= MONOLITH_CHANCE) {
            age = 0;
            display.println("Fleshy Mature Tree at " + location + " grows into a Fleshy Monolith!");
            return new FleshyMonolithStage();
        }

        return this;
    }

    @Override
    public char getDisplayChar() {
        return 'Y';
    }
}