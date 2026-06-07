package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Not lava. Not spikes. Not an elaborate trap. Just a perfectly flat surface
 * whose sole responsibility is preventing the {@code ContractedWorker} from
 * plummeting into the infinite vacuum of the Eclipse Nebula.
 *
 * @author Adrian Kristanto
 */
public class Floor extends Ground {

    /**
     * Constructs a new Floor tile.
     * Initializes the floor with a display character of '_' and the display name "Floor".
     * This terrain type imposes no movement restrictions or special effects on
     * actors traversing across it.
     */
    public Floor() {
        super('_', "Floor");
    }
}
