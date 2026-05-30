package game.highvoltage;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

public interface ChargeReactive {
    /**
     * Defines the structural or behavioral change when energy is absorbed.
     */
    void reactToCharge(Location location, Display display, String sourceName);
}