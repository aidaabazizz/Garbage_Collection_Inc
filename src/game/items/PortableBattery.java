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
import game.grounds.PoweredFloor;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeSource;
import game.highvoltage.MaterialCapability;

import java.util.List;

/**
 * REQ3: Portable Battery.
 * A mobile energy source that triggers permanent map transformations.
 */
public class PortableBattery extends Item implements ChargeSource {

    public PortableBattery() {
        // PDF Page 33: Uses the battery icon
        super("Portable Battery", 'ᯤ');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * PDF Page 34: The 3-Part "Indiscriminate Surge" logic.
     */
    @Override
    public void releaseCharge(Location location, Display display, String sourceName) {
        // 1. THE DUAL-LOGIC (Trigger vs. Morph)
        // Check if ground is already reactive (like a Puddle)
        ChargeReactive groundReactive = location.getGroundAs(ChargeReactive.class);
        if (groundReactive != null) {
            groundReactive.reactToCharge(location, display, sourceName);
        } else {
            // PDF Page 34: If not reactive and NOT already powered, turn it into a permanent outlet
            if (!location.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
                location.setGround(new PoweredFloor());
                display.println("The ground beneath " + location + " has been permanently electrified!");
            }
        }

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            ChargeReactive adjGround = adj.getGroundAs(ChargeReactive.class);
            if (adjGround != null) { adjGround.reactToCharge(adj, display, sourceName); }

            if (adj.containsAnActor()) {
                adj.getActor().asCapability(ChargeReactive.class)
                        .ifPresent(r -> r.reactToCharge(adj, display, sourceName));
            }
        }

        if (location.containsAnActor()) {
            Actor worker = location.getActor();
            List<ChargeReactive> reactives = worker.getInventory().getItemsAs(ChargeReactive.class);
            for (ChargeReactive r : reactives) {
                r.reactToCharge(location, display, sourceName);
            }
        }
    }

    /**
     * SOLID: Lifecycle cleanup. Batteries are consumed, Towers are not.
     */
    @Override
    public void consumeSource(Actor actor) {
        actor.getInventory().remove(this);
    }

    // REFINED BEST DESIGN
    @Override
    public ActionList allowableActions(Location location) {
        return getSurge();
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return getSurge();
    }

    private ActionList getSurge() {
        ActionList actions = new ActionList();
        actions.add(new GalvanicSurgeAction(this, "Portable Battery"));
        return actions;
    }
}