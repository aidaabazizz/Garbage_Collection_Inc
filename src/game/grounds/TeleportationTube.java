package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.capabilities.TeleportStrategy;
import game.teleportstrategies.TeleportTubeStrategy;

import java.util.List;

/**
 * A teleportation device ground that allow worker to quickly teleport to manually pre-determined locations.
 * It sets adjacent tiles on fire on where the worker is being teleported to for 2 turns.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class TeleportationTube extends Ground {

    /**
     * List of possible destination locations for teleportation
     * These locations are predetermined and fixed at the time of tube creation.
     */
    private final List<TeleportStrategy> strategies;

    /**
     * Construct a new Teleportation Tube with the specified destination locations
     * @param strategies a list of strategies that represents a destination
     */
    public TeleportationTube(List<TeleportStrategy> strategies) {
        super('Φ', "Teleportation Tube");
        this.strategies = strategies;
    }

    /**
     * Returns the list of allowable actions that an actor can perform while standing on this ground.
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return an action list that contains the teleport action if the actor can interact with the tube
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            for (TeleportStrategy strategy : strategies) {
                actions.add(new TeleportAction(strategy));
            }
        }
        return actions;
    }
}
