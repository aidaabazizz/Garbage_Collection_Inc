package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.teleportstrategies.BaseTeleportStrategy;
import game.utils.SpatialSearch;

import java.util.List;

/**
 * A class representing the final Mature stage of a Warper Tree.
 * This stage is characterized by its ability to automatically warp any worker
 * that stands in its surrounding tiles.
 *
 * @author Jewell Gomes
 */
public class WarperMatureStage extends AbstractTreeStage {
    // FIX: Change field type from TeleportStrategy to BaseTeleportStrategy
    private final BaseTeleportStrategy strategy;

    /**
     * Constructor Injection.
     * @param strategy The strategy defining how the tree warps workers.
     * @param displayChar The character displayed on the game map.
     */
    // FIX: Change constructor parameter to BaseTeleportStrategy
    public WarperMatureStage(BaseTeleportStrategy strategy, char displayChar) {
        super("Warper Mature", displayChar);
        this.strategy = strategy;
    }

    /**
     * Executes the behavior for the Mature Warper Tree.
     * Checks for a nearby worker each turn. If a worker is detected, it triggers
     * a TeleportAction and displays the resulting outcome.
     *
     * @param location The current location of the mature tree.
     * @return This stage instance (WarperMatureStage), as it is the final growth stage.
     */
    @Override
    public TreeStage execute(Location location) {
        List<Actor> targets = SpatialSearch.getNearbyWorkers(location);
        for (Actor worker : targets) {
            // This compiles perfectly now because TeleportAction accepts BaseTeleportStrategy
            TeleportAction warpAction = new TeleportAction(strategy);
            String result = warpAction.execute(worker, location.map());
            if (result != null && !result.isEmpty()) {
                display.println(result);
            }
        }
        return this;
    }
}