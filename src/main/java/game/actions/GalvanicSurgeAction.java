package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeSource;

/**
 * A custom Action class that allows an Actor to manually trigger a high-voltage galvanic discharge.
 * This class serves as the manual interface for Requirement 3 (High-Voltage Galvanic System).
 *
 * It is designed using the Dependency Inversion Principle (DIP), interacting with the
 * {@link ChargeSource} interface rather than concrete classes. This allows the same action
 * to handle both portable items (like Batteries) and stationary ground objects (like Tesla Coils).
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
     * Constructor for triggering portable items held in an inventory.
     * The center of the surge will be determined dynamically as the location of the Actor performing the action.
     *
     * @param source The ChargeSource (Item) being activated.
     * @param name   The name of the source for display purposes.
     */
    public GalvanicSurgeAction(ChargeSource source, String name) {
        this.source = source;
        this.name = name;
        this.center = null;
    }

    /**
     * Constructor for triggering stationary objects fixed to the map.
     * The center of the surge is fixed to the coordinate of the Ground object.
     *
     * @param source The ChargeSource (Ground) being activated.
     * @param name   The name of the source for display purposes.
     * @param loc    The fixed Location where the surge originates.
     */
    public GalvanicSurgeAction(ChargeSource source, String name, Location loc) {
        this.source = source;
        this.name = name;
        this.center = loc;
    }

    /**
     * Executes the manual galvanic surge.
     * Logic Flow:
     * 1. Determines the origin point of the surge (Fixed Location vs. Actor Location).
     * 2. Calls {@link ChargeSource#releaseCharge} to perform complex structural and environmental changes.
     * 3. Calls {@link ChargeSource#consumeSource} to handle object lifecycles (e.g., removing a battery).
     *
     * @param actor The Actor performing the action.
     * @param map   The GameMap the actor is currently on.
     * @return A descriptive string of the manual trigger to be displayed in the console.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Display display = new Display();
        Location locationToTrigger = (this.center != null) ? this.center : map.locationOf(actor);
        source.releaseCharge(locationToTrigger, display, this.name);
        source.consumeSource(actor);
        return actor + " manually triggers the " + name + " pulse!";
    }

    /**
     * Returns a string describing the action in the player's menu.
     *
     * @param actor The Actor performing the action.
     * @return A string formatted as "Manual Override: Trigger [Name]".
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Manual Override: Trigger " + name;
    }
}
