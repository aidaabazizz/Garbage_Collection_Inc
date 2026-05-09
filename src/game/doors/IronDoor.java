package game.doors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.AccessLevel;
import game.grounds.Door;
import game.grounds.Fire;

public class IronDoor extends Door {
    private static final int FIRE_DURATION = 2;
    private static final int ADJACENT_TILES = 1;

    public IronDoor() {
        super(AccessLevel.LEVEL_TWO);
    }

    @Override
    public char getDisplayChar() {
        if (isUnlocked()) {
            return '_';
        } else {
            return 'N';
        }
    }

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
