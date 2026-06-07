package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.teleportstrategies.FleshyMonolithStrategy;
import game.utils.SpatialSearch;

import java.util.List;

/**
 * Final stage of Fleshy Tree evolution on 99-deprecated map.
 * Cannot grow further.
 * Every turn, violently warps any adjacent worker to a random valid location on the same map.
 *
 * @author Aida
 */
public class FleshyMonolithStage implements TreeStage {
    private final Display display;

    public FleshyMonolithStage() {
        this.display = new Display();
    }

    @Override
    public TreeStage execute(Location location) {
        // Add null check - handles edge case for testing
        if (location == null) {
            display.println("Fleshy Monolith: Location is null, cannot execute");
            return this;
        }

        GameMap map = location.map();

        // Add map null check
        if (map == null) {
            display.println("Fleshy Monolith: Map is null, cannot execute");
            return this;
        }

        // Find adjacent workers using SpatialSearch
        List<Actor> adjacentWorkers = SpatialSearch.getNearbyWorkers(location);

        for (Actor worker : adjacentWorkers) {
            // Create teleportation strategy and teleport the worker
            FleshyMonolithStrategy strategy = new FleshyMonolithStrategy();
            strategy.teleport(worker, location);
        }

        // Monolith never changes state
        return this;
    }

    @Override
    public char getDisplayChar() {
        return 'H';
    }
}