package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;
import game.inventory.BasicInventory;

/**
 * A specialized NonPlayerCharacter that represents a bioelectrical "egg" or dormant entity.
 * This class is a core component of Requirement 3 (High-Voltage Galvanic System).
 *
 * The DormantStaticCreature remains completely inert and harmless until it is exposed to
 * a high-voltage galvanic charge. It acts as a "Resonator" in the galvanic ecosystem,
 * capable of detecting ENERGIZED terrain or receiving a direct surge from a ChargeSource.
 *
 * Complexity Proof (Rule 2):
 * This class demonstrates "Dynamic Evolution" — a structural map change where one
 * actor is permanently replaced by a more complex hostile predator (StaticStalker)
 * upon triggering an environmental condition.
 *
 * @author Jewell Gomes
 */
public class DormantStaticCreature extends NonPlayerCharacter implements ChargeReactive {
    /** The initial health points of the dormant entity. */
    private static final int INITIAL_HEALTH = 10;

    /**
     * Constructor for the DormantStaticCreature.
     * Initializes the entity with the 'O' symbol, representing a bioelectrical pod or egg.
     */
    public DormantStaticCreature() {
        super("Dormant Static Creature", 'O', INITIAL_HEALTH, new BasicInventory());
    }

    /**
     * Processes the dormant entity's turn.
     *
     * Instead of utilizing standard NPC behaviours, this entity performs an environmental check.
     * If the ground beneath its feet possesses the ENERGIZED capability, it automatically
     * triggers its own metamorphosis logic by calling its reactToCharge implementation.
     *
     * @param actions    A collection of available actions.
     * @param lastAction The action performed in the previous turn.
     * @param map        The current game map.
     * @param display    The terminal interface.
     * @return A DoNothingAction, as the entity has no independent movement or combat logic.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        // Actor-to-Ground interaction
        if (here.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            display.println(this + " absorbs charge from the ground!");
            String sourceName = "the " + here.getGround() + " beneath its feet";
            this.reactToCharge(here, display, sourceName);
        }
        return new DoNothingAction();
    }

    /**
     * Implements the ChargeReactive interface to handle high-voltage metamorphosis.
     *
     * When triggered, this method performs a structural map replacement:
     * 1. Logs the evolution event with high-visibility console output.
     * 2. Removes the dormant entity (this) from the GameMap.
     * 3. Spawns a brand-new StaticStalker actor at the same coordinate.
     *
     * This provides a "Metabolic Acceleration" effect where environmental energy
     * creates new threats dynamically.
     *
     * @param location   The location of the charge reception.
     * @param display    The terminal interface for outputting evolution messages.
     * @param sourceName The name of the energy source triggering the event.
     */
    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        display.println("\u001B[33m!!! The " + this + " is stimulated by " + sourceName + " and shatters !!!\u001B[0m");
        display.println("\u001B[33m>>> A Static Stalker has been born!\u001B[0m");
        location.map().removeActor(this);
        try {
            location.map().addActor(new StaticStalker(), location);
        } catch (Exception e) {
            display.println("Evolution failed: Tile at " + location + " is blocked.");
        }
    }
}