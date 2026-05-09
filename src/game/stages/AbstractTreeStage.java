package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.managers.CreatureSpawner;
import game.managers.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract implementation of TreeStage providing shared utility methods.
 * Fulfills the DRY principle by centralizing worker detection and dependency management.
 *
 * @author Jewell Gomes
 */
public abstract class AbstractTreeStage implements TreeStage {
    /** Random number generator for growth probabilities. */
    protected final Random random = new Random();
    /** Spawning service used to handle creature creation and REQ4 effects. */
    protected final Spawner spawner = new CreatureSpawner();
    /** By making this protected, all "Stage" subclasses inherit it automatically. */
    protected final Display display = new Display();

    /**
     * Identifies all nearby workers in the surrounding eight tiles.
     * @param location The current location of the flora.
     * @return The detected workers.
     */
    protected List<Actor> getNearbyWorkers(Location location) {
        List<Actor> workers = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                workers.add(adj.getActor());
            }
        }
        return workers;
    }
}
