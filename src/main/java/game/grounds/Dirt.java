package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * While other classes get to be security doors, mysterious flasks, or highly
 * stressed {@code ContractedWorker}s, this class humbly accepts its role as
 * the thing everyone walks all over.
 *
 * @author Adrian Kristanto
 */
public class Dirt extends Ground {

    /**
     * Constructs a new Dirt ground tile.
     * Initializes the ground with a display character of '.' and the display name "Dirt".
     * This terrain type allows any actor to traverse it without restrictions or
     * special movement costs.
     */
    public Dirt() {
        super('.', "Dirt");
    }
}
