package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.GalvanicSurgeAction;
import game.enums.MaterialCapability;
import game.highvoltage.*;

/**
 * A stationary high-voltage emitter representing a Tesla Ion Tower.
 *
 * The TeslaCoil is a central component of the High-Voltage Galvanic System (Requirement 3).
 * It is a dual-purpose entity:
 * 1. Emitter (ChargeSource): Automatically discharges a high-voltage pulse every 3 turns.
 * 2. Resonator (ChargeReactive): Reacts to external surges, allowing for "Overload"
 *    chain reactions between multiple towers.
 *
 * Complexity Proof (Requirement 3):
 * 1. Temporal Logic: Manages a 3-turn capacitor charging cycle.
 * 2. Geometric AoE: Uses Manhattan Distance math (|x| + |y| <= Radius) to calculate
 *    a diamond-shaped strike zone rather than a standard square.
 * 3. Overload Induction: Can be force-triggered by Lightning or other Coils,
 *    demonstrating ground-to-ground communication.
 * 4. Manual Override: Integrates with {@link GalvanicSurgeAction} to allow players
 *    to bypass the charging cycle.
 *
 * @author Jewell Gomes
 */
public class TeslaCoil extends Ground implements ChargeSource, ChargeReactive {
    /**
     * Internal capacitor countdown. Fires at 0.
     */
    private static final int MAX_CAPACITOR = 3;
    private static final int STRIKE_RADIUS = 2;
    private static final int DAMAGE = 3;
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";
    private final Display display = new Display();
    /** Current countdown to the next automatic discharge. */
    private int turnsToCharge = MAX_CAPACITOR;

    /**
     * Constructor for the TeslaCoil.
     * Initializes with the 'Ꮺ' icon and the ENERGIZED capability so that it can
     * act as a continuous power source for nearby magnetic items.
     */
    public TeslaCoil() {
        super('Ꮺ', "TeslaCoil");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    /**
     * Updates the charging state of the capacitor every turn.
     *
     * Logic:
     * 1. Decrements the internal capacitor timer.
     * 2. If the timer reaches zero, releases an automatic {@link GalvanicCharge}.
     * 3. Otherwise, prints a "humming" status message to indicate the current charge level.
     *
     * @param location The coordinate where the Tesla Coil is fixed.
     */
    @Override
    public void tick(Location location) {
        this.turnsToCharge--;
        if (this.turnsToCharge > 0) {
            display.println(PURPLE + "Tesla Coil is humming... (Charge level: " + (MAX_CAPACITOR - this.turnsToCharge) + "/" + MAX_CAPACITOR+ ")" + RESET);
        } else {
            GalvanicCharge autoBlast = new GalvanicCharge("the Tesla Coil Pulse", display, DAMAGE);
            this.releaseCharge(location, autoBlast);
        }
    }

    /**
     * Allows adjacent or standing actors to manually force a discharge.
     *
     * @param actor    The actor checking for actions.
     * @param location The location of the coil.
     * @param direction The direction from the actor to the coil.
     * @return A list containing the manual trigger action.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new GalvanicSurgeAction(this, "Tesla Coil", location, "Manual Override: Trigger Tesla Coil"));
        return actions;
    }

    /**
     * Implements the ChargeReactive interface to handle "Overloading."
     *
     * If hit by an external surge (Lightning, Battery, or another Coil), the tower
     * absorbs the energy and fires immediately.
     *
     * Safety Check:
     * Uses {@code charge.visit(location)} to ensure that two adjacent Tesla Coils
     * do not trigger each other in an infinite loop.
     *
     * @param location The coordinate of the coil being overloaded.
     * @param charge   The context of the incoming galvanic charge.
     */
    @Override
    public void reactToCharge(Location location, GalvanicCharge charge) {
        // if this Coil was already triggered in this wave, stop to prevent
        // coil-to-Coil infinite loops.
        if (!charge.visit(location)) {
            return;
        }
        charge.getDisplay().println("\u001B[35m The Tesla Coil is overloaded by " + charge.getSourceName() + " and discharges!\u001B[0m");
        this.releaseCharge(location, charge);
    }

    /**
     * Executes the high-voltage discharge.
     *
     * Logic Sequence:
     * 1. Resets the charging timer to its maximum value.
     * 2. Marks the origin location as visited to prevent self-targeting.
     * 3. Scans a diamond-shaped grid using the Manhattan Distance formula: |x| + |y| <= Radius.
     * 4. Propagates the surge to all tiles within this strike zone.
     *
     * @param location The origin of the discharge.
     * @param charge   The GalvanicCharge context defining the wave's properties.
     */
    @Override
    public void releaseCharge(Location location, GalvanicCharge charge) {
        this.turnsToCharge = MAX_CAPACITOR; // Reset the capacitor

        // 2. MARK ORIGIN AS VISITED (Safety Anchor)
        charge.visit(location);

        // Manhattan Radius 2 Diamond Scan.
        // We iterate through a 5x5 grid but use the absolute value formula |x| + |y| <= 2
        // to filter for a diamond shape rather than a square. This ensures a more
        // realistic electrical arc pattern.
        for (int x = -STRIKE_RADIUS; x <= STRIKE_RADIUS; x++) {
            for (int y = -STRIKE_RADIUS; y <= STRIKE_RADIUS; y++) {
                if (Math.abs(x) + Math.abs(y) <= STRIKE_RADIUS) {
                    int targetX = location.x() + x;
                    int targetY = location.y() + y;

                    // standard boundary safety check for the GameMap grid.
                    if (location.map().getXRange().contains(targetX) &&
                            location.map().getYRange().contains(targetY)) {

                        Location targetLoc = location.map().at(targetX, targetY);
                        propagateToTile(targetLoc, charge);
                    }
                }
            }
        }
    }

    /**
     * Propagates the surge to a specific tile within the strike radius.
     *
     * Impacts:
     * 1. Ground: Morphs Puddles into hazards or triggers Overloads in other towers.
     * 2. Actor: Deals damage and applies the Shocked status.
     * 3. Items: Triggers ChargeReactive items in inventories (e.g., Wallet pull).
     *
     * @param target The location to receive the charge.
     * @param charge The current charge context.
     */
    private void propagateToTile(Location target, GalvanicCharge charge) {
        // morph Puddles / Overload other Coils
        ChargeReactive reactiveGround = target.getGroundAs(ChargeReactive.class);
        if (reactiveGround != null) {
            // if the neighbor is a Coil, its own 'visit' guard will handle it.
            // if the neighbor is a Puddle, it will stack.
            reactiveGround.reactToCharge(target, charge);
        }

        // occupant impact (Actors and Items)
        // delegate to utility to ensure Bob only takes 3 damage ONCE.
        ChargeUtils.zapTile(target, charge, true);
    }
}
