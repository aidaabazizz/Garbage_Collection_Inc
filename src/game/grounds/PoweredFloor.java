package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;

/**
 * REQ3: Powered Floor.
 * A conductive surface created by a Portable Battery.
 * Propagates energy to inventory and neighbors while preventing infinite loops.
 */
public class PoweredFloor extends Ground implements ChargeReactive {

    public PoweredFloor() {
        super('⚜', "Powered Floor");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    /**
     * PDF Page 40: The Dual Propagation Logic.
     */
    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {

        // 1. INVENTORY PROPAGATION (PDF Page 41)
        // If an actor is standing here, pass the energy to their pocket (Magnetize Wallet)
        if (location.containsAnActor()) {
            Actor worker = location.getActor();
            worker.getInventory().getItemsAs(ChargeReactive.class)
                    .forEach(item -> item.reactToCharge(location, display, sourceName));
        }

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            if (!adj.getGround().hasAbility(MaterialCapability.ENERGIZED)) {

                ChargeReactive groundReactive = adj.getGroundAs(ChargeReactive.class);
                if (groundReactive != null) {
                    groundReactive.reactToCharge(adj, display, sourceName);
                }

                if (adj.containsAnActor()) {
                    adj.getActor().asCapability(ChargeReactive.class)
                            .ifPresent(r -> r.reactToCharge(adj, display, sourceName));
                }
            }
        }
    }
}