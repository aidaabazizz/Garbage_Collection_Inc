package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Cuttable;

/**
 * An action that allows an actor to cut a cuttable target using a Plasma Cutter.
 *
 * @author Victoria Tay Wen Xie
 */
public class CutAction extends Action {
    /**
     * The target that can be cut.
     */
    private final Cuttable target;
    /**
     * The name of the target.
     */
    private final String targetName;
    /**
     * The location of the target.
     */
    private final Location targetLocation;

    /**
     * A constructor to create a CutAction.
     *
     * @param target the cuttable target
     * @param targetName the name of the target
     * @param targetLocation the location of the target
     */
    public CutAction(Cuttable target, String targetName, Location targetLocation) {
        this.target = target;
        this.targetName = targetName;
        this.targetLocation = targetLocation;
    }

    /**
     * Executes the cutting action on the target.
     *
     * @param actor the actor performing the action
     * @param map the map containing the target
     * @return the result of the cut action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return target.executeCut(actor, map, targetLocation);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " cuts open the " + targetName + " with the Plasma Cutter";
    }
}
