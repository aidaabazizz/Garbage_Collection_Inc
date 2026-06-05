package game.doors;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CutAction;
import game.capabilities.Cuttable;
import game.enums.AccessLevel;
import game.grounds.Door;
import game.grounds.Floor;
import game.items.AluminiumScrap;

/**
 * An Aluminium Door that requires clearance level1 or higher. Unlocking
 * the door will shock the worker because of faulty electrical short-circuit
 * that causes 2 points of damage on actor.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class AluminiumDoor extends Door implements Cuttable {
    /** The amount of damage dealt to the worker when unlocking this door. */
    private static final int SHOCK_DAMAGE = 2;

    /** Constructor for a new Aluminium Door with level 1 clearance requirement */
    public AluminiumDoor() {
        super(AccessLevel.LEVEL_ONE);
    }

    /**
     * Returns the display character for the door based on its lock state.
     * @return '=' if locked, '_' if unlocked
     */
    @Override
    public char getDisplayChar() {
        if (isUnlocked()) {
            return '_';
        } else {
            return '=';
        }
    }

    /**
     * Applies the unlocking effect for the Aluminium Door.
     * @param actor the actor performing the unlock action
     * @param map the current game map containing the door
     * @return a descriptive message of the shock effect
     */
    @Override
    public String applyUnlockEffect(Actor actor, GameMap map) {
        actor.hurt(SHOCK_DAMAGE);
        return actor + " is shocked for " + SHOCK_DAMAGE + " damage from faulty electrical short-circuit!";
    }

    @Override
    public String executeCut(Actor actor, GameMap map, Location targetLocation) {
        targetLocation.setGround(new Floor());
        AluminiumScrap scrap = new AluminiumScrap();
        targetLocation.addItem(scrap);
        return actor + " uses the searing beam of the Plasma Cutter to completely slice through and destroy the Aluminium Door from an adjacent tile.\n " +
                "The door collapses into Aluminium Scraps.";
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        if (this.canBeCut(actor)) {
            actions.add(new CutAction(this, "Aluminium Door", location));
        }

        return actions;
    }

    @Override
    public String toString() {
        return "Aluminium Door";
    }
}
