package game.grounds;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeContext;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeUtils;
import game.enums.MaterialCapability;

/**
 * A specialized Ground tile representing a permanent conductive surface created by a galvanic surge.
 *
 * The PoweredFloor acts as a "Smart Conductor" in the High-Voltage Galvanic System (Requirement 3).
 * It serves a dual purpose in the galvanic ecosystem:
 * 1. Persistent Resonator: Provides the ENERGIZED capability, allowing equipment
 *    (like the Wallet) to draw power every turn while an actor stands on this tile.
 * 2. Active Propagator: Acts as a conductive bridge, passing high-voltage charges
 *    to the occupant's inventory and all neighboring tiles during a strike event.
 *
 * Complexity Proof (Requirement 3):
 * This class demonstrates "Indiscriminate Environmental Conduction." A single charge
 * hitting this floor triggers a cascading chain reaction involving Grounds, Actors,
 * and Items across multiple coordinates simultaneously.
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
     * The execution follows a "Cascade Logic" flow:
     * 1. Recursion Prevention: Checks the {@code GalvanicCharge} visited set to prevent
     *    infinite electrical loops between adjacent conductive tiles.
     * 2. Impact Phase: Utilizes {@link ChargeUtils#zapTile} to process damage,
     *    status effects, and item reactions for the actor currently on this tile.
     * 3. Propagation Phase: Iterates through all 8 surrounding exits to pass the
     *    charge to adjacent reactive Grounds (morphing/refreshing them) and
     *    nearby Actors (zapping them via arcing).
     *
     * Robustness (LO4):
     * The use of {@code charge.getVisited()} simulates realistic potential-difference
     * physics, ensuring energy flows outward from the source without crashing the
     * engine through stack overflow.
     *
     * @param location The coordinate of the PoweredFloor receiving the charge.
     * @param charge   The ChargeContext containing source info and propagation memory.
     */
    @Override
    public void reactToCharge(Location location, ChargeContext charge) {
        if (charge.getVisited().contains(location)) {
            return;
        }

        // 2. THE IMPACT (Bob and Inventory)
        // zapTile will perform the .visit() call, mark the tile, and deal damage.
        ChargeUtils.zapTile(location, charge, true);

        // 3. THE PROPAGATION (Neighbors)
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // Ground wave (Puddles stack, Floors continue)
            ChargeReactive neighbor = adj.getGroundAs(ChargeReactive.class);
            // ONLY print and call if the neighbor hasn't been visited yet!
            if (neighbor != null && !charge.getVisited().contains(adj)) {
                charge.getDisplay().println(" The " + this + " conducts to the " + neighbor + "!");
                neighbor.reactToCharge(adj, charge);
            }

            // Neighbor Actors (Zap people on dry dirt next to the floor)
            if (adj.containsAnActor()) {
                ChargeUtils.zapTile(adj, charge, true);
            }
        }
    }
}
