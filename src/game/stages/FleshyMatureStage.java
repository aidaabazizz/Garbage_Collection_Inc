package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AbstractTree;
import game.managers.Spawner;

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
     */
    public FleshyMatureStage(Spawner spawner) { super(spawner); }
    /**
     * The execute method runs every turn to check the environment around
     * the tree. It looks for any workers in the adjacent tiles and
     * attempts to spawn an undead creature for each one found. It
     * always returns itself to remain in the mature state.
     */
    @Override
    public TreeStage execute(Location location) {
        List<Actor> targets = getNearbyWorkers(location);
        if (!targets.isEmpty()) {
            display.println("Fleshy Mature Tree at " + location + " is producing Undead!");
        }
        for (Actor worker : targets) {
            spawner.spawnUndead(location);
        }
        return this;
    }

    /**
     * This method returns the uppercase letter Y which is the visual
     * icon used to show a mature fleshy tree on the game world map.
     */
    @Override
    public char getDisplayChar() { return 'Y'; }
}
