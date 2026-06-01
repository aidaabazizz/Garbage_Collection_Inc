package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.GalvanicSurgeAction;
import game.enums.ItemStatistics;
import game.grounds.IonizedBarrier;
import game.grounds.PoweredFloor;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeSource;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ShockedStatus;

import java.util.List;

/**
 * A mobile high-voltage energy source that serves as a catalyst for map transformations.
 *
 * The PortableBattery is a cornerstone of Requirement 3. It is designed to be a
 * "Strategic Trigger" that allows the player to manually manipulate the facility environment.
 *
 * Complexity Proof:
 * 1. Structural Terrain Morphing: Converts standard floors into permanent power outlets.
 * 2. Dynamic Structural Engineering: Spawns temporary blocking barriers (IonizedBarrier).
 * 3. Indiscriminate Area Effect: Simultaneously triggers evolution in NPCs, damage
 *    in enemies, and state changes in inventory items.
 * 4. Branching Logic: Uses a high-level conduction hierarchy to decide between
 *    transforming grounds or triggering existing hazards.
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
     * Executes a massive galvanic surge originating from the battery's current location.
     *
     * This method implements a 3-stage "Indiscriminate Surge" pattern:
     * 1. Ground Morphing: The tile beneath the user is permanently transformed.
     * 2. Environmental Propagation: Surrounding tiles are either morphed into
     *    barriers, evolved into predators, or shocked as enemies.
     * 3. Inventory Conduction: Reactive items in the user's pocket are remotely powered.
     *
     * @param location   The origin coordinate of the surge.
     * @param display    The terminal interface for outputting surge events.
     * @param sourceName The display name of this source ("Portable Battery").
     */
    @Override
    public void releaseCharge(Location location, Display display, String sourceName) {
        /*
         * TARGET GROUND MORPHING
         * We use getGroundAs to prioritize triggering existing hazards (like Puddles).
         * If the ground is not reactive, we structurally replace it with a PoweredFloor.
         */
        ChargeReactive groundReactive = location.getGroundAs(ChargeReactive.class);
        if (groundReactive != null) {
            groundReactive.reactToCharge(location, display, sourceName);
        } else {
            // only morph if it's not already powered to prevent redundant object creation.
            if (!location.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
                location.setGround(new PoweredFloor());
                display.println("The ground beneath " + location + " has been permanently electrified!");
            }
        }

        display.println("Static energy solidifies into a protective Ionized Barrier around Bob!");

        /*
         * AOE PROPAGATION (Neighbors)
         * We iterate through the 8 exits to simulate a kinetic blast radius.
         */
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // ground interaction (Morphing into Hazards or Barriers)
            ChargeReactive adjGround = adj.getGroundAs(ChargeReactive.class);
            if (adjGround != null) {
                adjGround.reactToCharge(adj, display, sourceName);
            }
            else if (adj.getGround().canActorEnter(null)) {
                /*
                 * Dynamic Barrier Spawning.
                 * If the neighbor is a standard passable floor, we replace it
                 * with an IonizedBarrier to create a temporary defensive cage.
                 */
                adj.setGround(new IonizedBarrier());
            }

            // actor interaction (Metamorphosis vs. Combat)
            if (adj.containsAnActor()) {
                Actor victim = adj.getActor();

                /*
                 * we use ifPresentOrElse to ensure an actor
                 * either evolves (Reactive path) OR takes damage (Standard path).
                 * This prevents the "Double-Zap" bug.
                 */
                victim.asCapability(ChargeReactive.class).ifPresentOrElse(
                        // biological transformation (e.g. Egg -> Stalker)
                        reactive -> reactive.reactToCharge(adj, display, sourceName),

                        // environmental damage (e.g. Undead/Human)
                        () -> {
                            victim.hurt(3);
                            victim.addStatus(new ShockedStatus(2));
                            display.println(victim + " is zapped by the battery surge!");
                        }
                );
            }
        }

        /*
         * inventory zap (pocket conduction)
         * Bob acts as a bridge. The electricity travels from the battery
         * through his suit and powers his other tools (like the Wallet).
         */
        if (location.containsAnActor()) {
            Actor worker = location.getActor();
            List<ChargeReactive> reactives = worker.getInventory().getItemsAs(ChargeReactive.class);
            for (ChargeReactive r : reactives) {
                r.reactToCharge(location, display, sourceName);
            }
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
     * Helper to centralize manual surge action generation.
     * @return An ActionList containing the GalvanicSurgeAction.
     */
    private ActionList getSurge() {
        ActionList actions = new ActionList();
        actions.add(new GalvanicSurgeAction(this, "Portable Battery"));
        return actions;
    }
}