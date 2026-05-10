package game.doors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enums.AccessLevel;
import game.grounds.Door;

/**
 * A titanium door that requires Level 3 clearance to unlock.
 * The effect after unlocking it will be decontamination sequence that will
 * in return heals the worker by 5 health points.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class TitaniumDoor extends Door {
    /** The amount of health points restored when unlocking this door*/
    private static final int HEAL_AMOUNT = 5;

    /**
     * Constructor for a new Titanium Door with level 3 clearance requirement
     */
    public TitaniumDoor() {
        super(AccessLevel.LEVEL_THREE);
    }

    /**
     * Returns the display character for the door based on its lock state.
     * When locked, displays 'M', when unlocked, displays '_'
     * @return the display character for the door on its lock state
     */
    @Override
    public char getDisplayChar() {
        if (isUnlocked()) {
            return '_';
        } else {
            return 'M';
        }
    }

    /**
     * This will apply the unlocking effect of the Titanium Door where decontamination
     * will happen and worker's health point will increase by 5.
     * @param actor the actor performing the unlock action
     * @param map the current game map containing the door
     * @return a descriptive message indicating the healing effect
     */
    @Override
    public String applyUnlockEffect(Actor actor, GameMap map) {
        actor.heal(HEAL_AMOUNT);
        return actor + " is healed for " + HEAL_AMOUNT + " health point from decontamination.";
    }
}
