package game.doors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enums.AccessLevel;
import game.grounds.Door;

/**
 * An Aluminium Door that requires clearance level1 or higher. Unlocking
 * the door will shock the worker because of faulty electrical short-circuit
 * that causes 2 points of damage on actor.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class AluminiumDoor extends Door {
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
}
