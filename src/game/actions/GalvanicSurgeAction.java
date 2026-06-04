package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeContext;
import game.highvoltage.ChargeSource;
import game.highvoltage.GalvanicCharge;

/**
 * A custom Action class that allows an Actor to manually trigger a high-voltage galvanic discharge.
 * This class serves as the manual interface for the High-Voltage Galvanic System (Requirement 3).
 *
 * It is designed using the Dependency Inversion Principle (DIP), interacting with the
 * {@link ChargeSource} interface rather than concrete classes. This allows the same action
 * to generically handle both portable items (like Batteries) and stationary ground objects
 * (like Tesla Coils).
 *
 * @author Jewell Gomes
 */
public class GalvanicSurgeAction extends Action {
    /**
     * The underlying energy source to be triggered.
     */
    private final ChargeSource source;
    /**
     * The name of the source, used for display messages (e.g., "Portable Battery").
     */
    private final String name;
    /**
     * The fixed location of the surge. If null, the surge originates from the Actor's current position.
     */
    private final Location center;
    /**
     * The custom string to be displayed in the player's action menu.
     */
    private final String menuText;
    /**
     * The base damage dealt by a manually triggered surge.
     */
    private static final int MANUAL_SURGE_DAMAGE = 3;

    /**
     * Constructor for triggering portable items held in an actor's inventory.
     * The center of the surge is determined dynamically as the location of the Actor
     * performing the action at the time of execution.
     *
     * @param source   The ChargeSource (Item) being activated.
     * @param name     The name of the source for display purposes.
     * @param menuText The text to appear in the action menu (e.g., "Smash the Battery").
     */
    public GalvanicSurgeAction(ChargeSource source, String name, String menuText) {
        this.source = source;
        this.name = name;
        this.center = null;
        this.menuText = menuText;
    }

    /**
     * Constructor for triggering stationary objects fixed to the map.
     * The center of the surge is fixed to the coordinate of the Ground object.
     *
     * @param source   The ChargeSource (Ground) being activated.
     * @param name     The name of the source for display purposes.
     * @param loc      The fixed Location where the surge originates.
     * @param menuText The text to appear in the action menu (e.g., "Trigger Tesla Coil").
     */
    public GalvanicSurgeAction(ChargeSource source, String name, Location loc, String menuText) {
        this.source = source;
        this.name = name;
        this.center = loc;
        this.menuText = menuText;
    }

    /**
     * Executes the manual galvanic surge.
     *
     * The logic flow is as follows:
     * 1. Determines the origin point: uses the fixed location (for stationary objects)
     *    or the actor's current location (for portable items).
     * 2. Creates a {@link GalvanicCharge} context using the {@code MANUAL_SURGE_DAMAGE}.
     * 3. Calls {@link ChargeSource#releaseCharge} to propagate the electrical wave.
     * 4. Calls {@link ChargeSource#consumeSource} to handle the lifecycle of the
     *    emitter (e.g., removing a battery from inventory).
     *
     * @param actor The Actor performing the action.
     * @param map   The GameMap the actor is currently on.
     * @return A descriptive string indicating the manual trigger was successful.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location locationToTrigger = (this.center != null) ? this.center : map.locationOf(actor);
        // Create the Wave Context ( Memory + Display + Name)
        ChargeContext charge = new GalvanicCharge(this.name, new Display(), MANUAL_SURGE_DAMAGE);

        // Execute the surge using the new signature
        source.releaseCharge(locationToTrigger, charge);
        source.consumeSource(actor);
        return actor + " manually triggers the " + name + " pulse!";
    }

    /**
     * Returns the descriptive string for this action to be displayed in the player's menu.
     *
     * @param actor The Actor performing the action.
     * @return The {@code menuText} provided during construction.
     */
    @Override
    public String menuDescription(Actor actor) {
        return menuText;
    }
}
