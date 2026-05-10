package game.doors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enums.AccessLevel;
import game.grounds.Door;

public class TitaniumDoor extends Door {
    private static final int HEAL_AMOUNT = 5;

    public TitaniumDoor() {
        super(AccessLevel.LEVEL_THREE);
    }

    @Override
    public char getDisplayChar() {
        if (isUnlocked()) {
            return '_';
        } else {
            return 'M';
        }
    }

    @Override
    public String applyUnlockEffect(Actor actor, GameMap map) {
        actor.heal(HEAL_AMOUNT);
        return actor + " is healed for " + HEAL_AMOUNT + " health point from decontamination.";
    }
}
