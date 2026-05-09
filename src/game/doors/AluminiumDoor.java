package game.doors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enums.AccessLevel;
import game.grounds.Door;

public class AluminiumDoor extends Door {
    private static final int SHOCK_DAMAGE = 2;

    public AluminiumDoor() {
        super(AccessLevel.LEVEL_ONE);
    }

    @Override
    public char getDisplayChar() {
        if (isUnlocked()) {
            return '_';
        } else {
            return '=';
        }
    }

    @Override
    public String applyUnlockEffect(Actor actor, GameMap map) {
        actor.hurt(SHOCK_DAMAGE);
        return actor + " is shocked for " + SHOCK_DAMAGE + " damage from faulty electrical short-circuit!";
    }
}
