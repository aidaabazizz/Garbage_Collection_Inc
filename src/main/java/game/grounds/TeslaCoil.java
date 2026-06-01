package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.GalvanicSurgeAction;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeSource;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ShockedStatus;

/**
 * A stationary high-voltage emitter representing a Tesla Ion Tower.
 *
 * The TeslaCoil is the most complex component of Requirement 3. It serves as both a
 * {@link ChargeSource} (emitting energy) and a {@link ChargeReactive} object (reacting
 * to external strikes), creating an "Overload" mechanism.
 *
 * Complexity Proof (Rule 2):
 * 1. Temporal Logic: Manages its own 3-turn capacitor charging cycle.
 * 2. Geometric AoE: Uses Manhattan Distance math to calculate a diamond-shaped strike zone.
 * 3. Indiscriminate Conduction: A single discharge triggers grounds, actors, and inventories
 *    simultaneously within its radius.
 * 4. Manual Override: Provides a player-facing action to bypass the charging timer.
 *
 * @author Jewell Gomes
 */
public class TeslaCoil extends Ground implements ChargeSource, ChargeReactive {
    /** Internal capacitor countdown. Fires at 0. */
    private int turnsToCharge = 3;
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";

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
     * Updates the charging state of the coil every turn.
     * Decrements the capacitor and prints a status message to the console.
     *
     * @param location The coordinate of the TeslaCoil.
     */
    @Override
    public void tick(Location location) {
        Display display = new Display();
        this.turnsToCharge--;
        if (this.turnsToCharge > 0) {
            display.println(PURPLE + "Tesla Coil is humming... (Charge level: " + (3 - this.turnsToCharge) + "/3)" + RESET);
        } else {
            display.println(PURPLE + "The Tesla Coil reaches critical mass and discharges a massive pulse!" + RESET);
            this.releaseCharge(location, display, "the Tesla Coil Pulse");
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
        actions.add(new GalvanicSurgeAction(this, "Tesla Coil", location));
        return actions;
    }

    /**
     * Implements the ChargeReactive interface to handle "Overloading".
     * If hit by lightning or a battery surge, the coil fires immediately
     * regardless of its current charge level.
     *
     * @param location   The coordinate of the coil.
     * @param display    The terminal interface.
     * @param sourceName The name of the striking source.
     */
    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        display.println("\u001B[35m!!! The Tesla Coil is overloaded by " + sourceName + " and discharges !!!\u001B[0m");
        this.releaseCharge(location, display, "the Tesla Coil Pulse");
    }

    /**
     * Executes the high-voltage discharge across the map.
     * Resets the charging timer and iterates through the Manhattan diamond zone.
     *
     * @param location   The origin of the discharge.
     * @param display    The terminal interface.
     * @param sourceName The name of the discharge event.
     */
    @Override
    public void releaseCharge(Location location, Display display, String sourceName) {
        this.turnsToCharge = 3; // Reset the capacitor

        // Manhattan Radius 2 Diamond Scan.
        // We iterate through a 5x5 grid but use the absolute value formula |x| + |y| <= 2
        // to filter for a diamond shape rather than a square. This ensures a more
        // realistic electrical arc pattern.
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                if (Math.abs(x) + Math.abs(y) <= 2) {
                    int targetX = location.x() + x;
                    int targetY = location.y() + y;

                    // standard boundary safety check for the GameMap grid.
                    if (location.map().getXRange().contains(targetX) &&
                            location.map().getYRange().contains(targetY)) {

                        Location targetLoc = location.map().at(targetX, targetY);
                        triggerChainReaction(targetLoc, display, sourceName);
                    }
                }
            }
        }
    }

    /**
     * Final stage of the surge. Uses Interface Discovery (DIP) to trigger
     * diverse effects on Grounds, Actors, and Items simultaneously.
     *
     * @param target     The specific tile being zapped.
     * @param display    The terminal interface.
     * @param sourceName The name of the energy event.
     */
    private void triggerChainReaction(Location target, Display display, String sourceName) {
        // we only trigger Ground reactions if the tile is NOT
        // already ENERGIZED. This prevents infinite recursive loops between
        // connected power sources (like two Tesla Coils).
        if (!target.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            ChargeReactive groundReactive = target.getGroundAs(ChargeReactive.class);
            if (groundReactive != null) {
                groundReactive.reactToCharge(target, display, sourceName);
            }
        }

        // standard Actor-based combat and biological triggers.
        if (target.containsAnActor()) {
            Actor victim = target.getActor();

            display.println(PURPLE + "⚡ " + victim + " is caught in " + sourceName + "!" + RESET);
            // direct HP damage
            victim.hurt(3);
            victim.addStatus(new ShockedStatus(2));

            // trigger specific actor reactions (e.g., Egg -> Stalker)
            victim.asCapability(ChargeReactive.class).ifPresent(r -> r.reactToCharge(target, display, sourceName));

            // trigger inventory item reactions (e.g., Wallet pull)
            victim.getInventory().getItemsAs(ChargeReactive.class)
                    .forEach(r -> r.reactToCharge(target, display, sourceName));
        }
    }
}
