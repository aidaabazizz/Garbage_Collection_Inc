package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.GalvanicSurgeAction;
import game.enums.ItemStatistics;
import game.enums.MaterialCapability;
import game.grounds.IonizedBarrier;
import game.grounds.PoweredFloor;
import game.highvoltage.*;

/**
 * A mobile high-voltage energy source that serves as a catalyst for map transformations.
 *
 * The PortableBattery is a cornerstone of the High-Voltage Galvanic System (Requirement 3).
 * It is designed as a "Strategic Trigger" that allows actors to manually manipulate
 * the facility's environment through controlled electrical surges.
 *
 * Complexity Proof (Requirement 3):
 * 1. Structural Terrain Morphing: Converts standard floors into permanent {@link PoweredFloor} outlets.
 * 2. Dynamic Structural Engineering: Spawns temporary blocking {@link IonizedBarrier} tiles
 *    around the user to create defensive perimeters.
 * 3. Indiscriminate Area Effect: Simultaneously triggers metabolic evolution in NPCs,
 *    combat damage in enemies, and state changes in items within the blast radius.
 * 4. Branching Conduction Logic: Evaluates ground types at the center and periphery
 *    to decide whether to transform the map or simply propagate the charge.
 *
 * @author Jewell Gomes
 */
public class PortableBattery extends Item implements ChargeSource {

    /**
     * Constructor for the PortableBattery.
     * Initializes with the battery icon ('ᯤ') and sets its weight statistic.
     */
    public PortableBattery() {
        super("Portable Battery", 'ᯤ');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * Executes a massive galvanic surge originating from the battery's location.
     *
     * This method implements a 3-stage "Indiscriminate Surge" pattern:
     * 1. Ground Morphing (Center): If the floor beneath the user is not already energized
     *    or reactive, it is permanently transformed into a {@link PoweredFloor}.
     * 2. Local Impact (Center): Zaps the actor and items at the origin point.
     * 3. Environmental Propagation (AOE): Surrounding tiles are transformed into
     *    {@link IonizedBarrier} walls (if clear), and all occupants in the 8-neighbor
     *    radius receive a high-voltage strike.
     *
     * @param location The origin coordinate where the battery is activated.
     * @param charge   The GalvanicCharge context containing source metadata and damage payload.
     */
    @Override
    public void releaseCharge(Location location, GalvanicCharge charge) {
        if (location.getGroundAs(ChargeReactive.class) == null) {
            if (!location.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
                location.setGround(new PoweredFloor());
                charge.getDisplay().println("The ground beneath " + location + " has been permanently electrified!");
            }
        }
        // 1. CENTER TILE LOGIC (Unique to Battery)
        // Always zap the center tile first
        ChargeUtils.zapTile(location, charge, true);

        charge.getDisplay().println("\u001B[36m Static energy solidifies into protective Ionized Barriers around the user!\u001B[0m");

        // 2. AOE PROPAGATION (Neighbors)
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            if (adj.getGroundAs(ChargeReactive.class) == null && adj.getGround().canActorEnter(null)) {
                adj.setGround(new IonizedBarrier());
            }

            // C. Universal Impact (Zaps anyone standing on the neighbor tiles)
            ChargeUtils.zapTile(adj, charge, true);
        }
    }

    /**
     * SOLID: Single Responsibility Principle.
     * Handles the specific cleanup requirements for a single-use battery.
     *
     * @param actor The actor whose inventory the battery should be removed from.
     */
    @Override
    public void consumeSource(Actor actor) {
        actor.getInventory().remove(this);
    }
    /**
     * Overrides the ground-based interaction logic to provide the manual surge action.
     *
     * This implementation allows an Actor to interact with the battery while it is
     * lying on a map tile. It fulfills the "Strategic Trigger" requirement by
     * enabling the player to stand over a dropped battery and activate it
     * as an environmental trap.
     *
     * @param location The current coordinate of the battery on the GameMap.
     * @return An ActionList containing the {@link game.actions.GalvanicSurgeAction}.
     */
    @Override
    public ActionList allowableActions(Location location) {
        return getSurge();
    }
    /**
     * Overrides the inventory-based interaction logic to provide the manual surge action.
     *
     * This is the primary strategic interface for the battery. It allows the holder
     * to carry the item across different facility zones and choose the optimal
     * moment to "smash" it, triggering the high-voltage chain reaction directly
     * from their inventory.
     *
     * @param owner The Actor currently carrying the battery in their backpack.
     * @param map   The GameMap the owner is currently navigating.
     * @return An ActionList containing the {@link game.actions.GalvanicSurgeAction}.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return getSurge();
    }

    /**
     * Helper to centralize the creation of the manual surge action.
     * The action is created with a specialized description:
     * "Smashes the Portable Battery to release a surge!"
     *
     * @return An ActionList containing a new GalvanicSurgeAction.
     */
    private ActionList getSurge() {
        ActionList actions = new ActionList();
        actions.add(new GalvanicSurgeAction(this, "Portable Battery", "Smashes the Portable Battery to release a surge!"));
        return actions;
    }
}