package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;

/**
 * A specialized Ground tile representing a permanent conductive surface created by a galvanic surge.
 *
 * The PoweredFloor acts as a "Smart Conductor" in the High-Voltage Galvanic System (REQ3).
 * It is a Resonator that serves two primary purposes:
 * 1. Continuous Power: Provides the ENERGIZED capability so that items (like the Wallet)
 *    can draw power every turn while Bob stands on this tile.
 * 2. Energy Propagation: Acts as a bridge, passing high-voltage charges to an actor's
 *    inventory and neighboring tiles during a strike event.
 *
 * Complexity Proof (Rule 2):
 * This class demonstrates "Indiscriminate Environmental Conduction." A single charge
 * hitting this floor triggers a cascading chain reaction involving Grounds, Actors,
 * and Items simultaneously.
 *
 * @author Jewell Gomes
 */
public class PoweredFloor extends Ground implements ChargeReactive {

    /**
     * Constructor for the PoweredFloor.
     * Initializes the ground with the fleur-de-lis icon ('⚜').
     * Sets the ENERGIZED capability to identify the tile as a persistent power source.
     */
    public PoweredFloor() {
        super('⚜', "Powered Floor");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    /**
     * Implements the ChargeReactive interface to handle energy reception and propagation.
     *
     * Logic Flow (The Chain Reaction):
     * 1. Inventory Propagation: If an actor is standing on the tile, the charge is passed
     *    directly to all ChargeReactive items in their inventory (e.g., triggering a Wallet pull).
     * 2. Neighbor Conduction: Iterates through all 8 surrounding exits to pass the charge
     *    to adjacent reactive Grounds and Actors.
     *
     * Safety Gate (LO4 Robustness):
     * Before propagating to a neighbor, the code checks if the adjacent Ground is already
     * ENERGIZED. This prevents infinite recursive loops between conductive tiles,
     * simulating realistic potential-difference physics and protecting the game from crashes.
     *
     * @param location   The coordinate of the PoweredFloor.
     * @param display    The terminal interface for outputting surge events.
     * @param sourceName The name of the energy source triggering the conduction.
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