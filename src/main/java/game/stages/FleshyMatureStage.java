package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;
import game.utils.SpatialSearch;

import java.util.List;

/**
 * This class represents the final mature stage of the fleshy tree which
 * is a biological hazard found on the moon. It has reached its full
 * growth and is now responsible for producing undead creatures when
 * human workers get too close.
 */
public class FleshyMatureStage extends FleshyTreeStage {
    /**
     * Constructor for the Mature stage.
     * @param spawner The spawning manager used to handle the creation of Undead creatures.
     * @param displayChar The display character for the fleshy mature stage.
     */
    public FleshyMatureStage(Spawner spawner, char displayChar) {
        super(spawner, "Fleshy Mature", displayChar);
    }
    /**
     * The execute method runs every turn to check the environment around
     * the tree. It looks for any workers in the adjacent tiles and
     * attempts to spawn an undead creature for each one found. It
     * always returns itself to remain in the mature state.
     *
     * @param location  The current map location of the tree.
     */
    @Override
    public TreeStage execute(Location location) {
        List<Actor> targets = SpatialSearch.getNearbyWorkers(location);
        if (!targets.isEmpty()) {
            display.println(name + " at " + location + " is producing Undead!");
        }
        for (Actor worker : targets) {
            spawner.spawnUndead(location);
        }
        return this;
    }
}
