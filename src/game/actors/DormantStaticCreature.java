package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.GalvanicCharge;
import game.enums.MaterialCapability;
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
     * The base damage value used when the creature creates a local charge context
     * upon absorbing energy from the ground.
     */
    private static final int DAMAGE = 1;
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
     * Instead of moving or attacking, this entity performs an environmental check on its
     * current location. If the ground possesses the ENERGIZED capability, the creature
     * absorbs the charge and triggers its metamorphosis logic.
     *
     * @param actions    A collection of available actions (ignored by this actor).
     * @param lastAction The action performed in the previous turn.
     * @param map        The current game map.
     * @param display    The terminal interface for logging charge absorption.
     * @return A DoNothingAction, as the entity remains stationary until it evolves.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        // Actor-to-Ground interaction
        if (here.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            display.println(this + " absorbs charge from the ground!");
            String sourceName = "the " + here.getGround() + " beneath its feet";
            GalvanicCharge passiveWave = new GalvanicCharge(sourceName, display, DAMAGE);
            this.reactToCharge(here, passiveWave);
        }
        return new DoNothingAction();
    }

    /**
     * Implements the ChargeReactive interface to handle high-voltage metamorphosis.
     *
     * When a charge is received (either from the ground or a direct pulse), this method:
     * 1. Logs the evolution event with high-visibility console output.
     * 2. Removes the dormant entity from the GameMap.
     * 3. Spawns a new StaticStalker actor at the same location.
     *
     * This simulates "Metabolic Acceleration," where external energy catalyzes
     * the birth of a new threat.
     *
     * @param location The location where the charge was received.
     * @param charge   The GalvanicCharge object containing source details and display logic.
     */
    @Override
    public void reactToCharge(Location location, GalvanicCharge charge) {
        Display display = charge.getDisplay();
        display.println("\u001B[33m The " + this + " is stimulated by " + charge.getSourceName() + " and shatters!\u001B[0m");
        display.println("\u001B[33m A Static Stalker has been born!\u001B[0m");
        location.map().removeActor(this);
        try {
            location.map().addActor(new StaticStalker(), location);
        } catch (Exception e) {
            display.println("Evolution failed: Tile at " + location + " is blocked.");
        }
    }
}