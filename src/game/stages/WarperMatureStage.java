package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.capabilities.TeleportStrategy;
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
    private final TeleportStrategy strategy;

    /**
     * Constructor Injection.
     * @param strategy The strategy defining how the tree warps workers.
     */
    public WarperMatureStage(TeleportStrategy strategy) {
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
            TeleportAction warpAction = new TeleportAction(strategy);
            String result = warpAction.execute(worker, location.map());
            if (result != null && !result.isEmpty()) {
                display.println(result);
            }
        }
        return this;
    }

    /**
     * Returns the display character for the Mature Warper Tree.
     *
     * @return The character 'W'.
     */
    @Override
    public char getDisplayChar() { return 'W'; }
}
