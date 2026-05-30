package game.highvoltage;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

public interface ChargeSource {
    /**
     * Triggers the release of energy at a specific location.
     */
    void releaseCharge(Location location, Display display, String sourceName);

    /**
     * SOLID: Default method handles cleanup.
     * Items will override this to remove themselves; Grounds will inherit the no-op.
     */
    default void consumeSource(Actor actor) {}
}
