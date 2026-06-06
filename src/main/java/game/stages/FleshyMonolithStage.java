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
        GameMap map = location.map();

        // Find adjacent workers using SpatialSearch (NO instanceof!)
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
        return 'M';
    }
}