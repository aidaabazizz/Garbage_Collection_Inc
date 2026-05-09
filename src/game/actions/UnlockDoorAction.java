package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Unlockable;

/**
 * An action dedicated to modifying the state of locked environment obstacles.
 * This class facilitates the interaction between an actor possessing security credentials
 * and an object that restricts movement through the facility. It primarily handles the
 * transition of the target from a restricted state to an accessible state.
 *
 * @author Jewell Gomes
 */
public class UnlockDoorAction extends Action {

    private final Unlockable target;
    private final String direction;

    /**
     * Constructor to initialize the unlocking action with a target and its relative position.
     *
     * @param target    The object implementing the Unlockable interface.
     * @param direction The direction of the target from the actor's current location.
     */
    public UnlockDoorAction(Unlockable target, String direction) {
        this.target = target;
        this.direction = direction;
    }

    /**
     * Executes the security clearance logic to unlock the target.
     * This method invokes the unlock routine on the target object, changing its
     * internal state to allow passage. It assumes the actor has the required
     * prerequisites to trigger this action.
     *
     * @param actor The Actor performing the security swipe.
     * @param map   The GameMap where the actor and target are located.
     * @return A string confirming the successful bypass of the security lock in the specified direction.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        target.unlock();
        String unlockEffect = target.applyUnlockEffect(actor, map);
        return actor + " swipes the card and unlocks the door to the " + direction;
    }

    /**
     * Provides a description of the security bypass option for the player menu.
     *
     * @param actor The Actor performing the action.
     * @return A string representing the command to unlock the target in the given direction.
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks the door to the " + direction;
    }
}
