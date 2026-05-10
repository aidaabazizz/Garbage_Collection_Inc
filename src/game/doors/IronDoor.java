package game.doors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;
import game.grounds.Door;
import game.grounds.Fire;

/**
 * Iron door requires level 2 or higher clearance.
 * Unlocking iron door causes the mechanism to overheat, setting adjavent tiles on fire
 * for 2 turns.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class IronDoor extends Door {
    /** Fire duration in game turns*/
    private static final int FIRE_DURATION = 2;

    /** Radius for adjacent tiles */
    private static final int ADJACENT_TILES = 1;

    /**
     * Constructor for the Iron door with level 2 clearance
     */
    public IronDoor() {
        super(AccessLevel.LEVEL_TWO);
    }

    /**
     * Returns 'N' when locked, '_' when unlocked.
     * @return display character based on lock state
     */
    @Override
    public char getDisplayChar() {
        if (isUnlocked()) {
            return '_';
        } else {
            return 'N';
        }
    }

    /**
     * This will set adjacent tiles on fire for 2 game turns when the door is unlocked
     * @param actor the actor performing the unlock action
     * @param map the current game map containing the door
     * @return description of the overheating effect after unlocking it
     */
    @Override
    public String applyUnlockEffect(Actor actor, GameMap map) {
        // gets the current position of the worker who unlocked the door
        Location actorLocation = map.locationOf(actor);
        for (Exit exit : actorLocation.getExits()) {
            Location doorLocation = exit.getDestination();
            if (doorLocation.getGround() == this) {
                for (Location adjacent : doorLocation.getNearbyLocations(ADJACENT_TILES)) {
                    Ground ground = adjacent.getGround();
                    if (ground.canActorEnter(actor)) {
                        adjacent.setGround(new Fire(ground, FIRE_DURATION));
                    }
                }
                return "The door overheats, setting adjacent floor tiles on fire!";
            }
        }
        return "The door overheats!";
    }
}
