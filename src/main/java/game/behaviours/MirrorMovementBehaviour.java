// game.behaviours/MirrorMovementBehaviour.java
package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Causes the actor to mirror the movement of the nearest worker.
 * When the target moves in one direction, this actor moves in the opposite direction.
 *
 * @author Aida
 */
public class MirrorMovementBehaviour implements Behaviour<Actor, Action> {

    private Actor target;
    private Location previousTargetLocation;

    public MirrorMovementBehaviour(Actor target) {
        this.target = target;
    }

    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();
        Location targetLocation = map.locationOf(target);

        // First tick - store initial position and wander
        if (previousTargetLocation == null) {
            previousTargetLocation = targetLocation;
            return getRandomMoveAction(actor, location);
        }

        // Calculate direction the target moved
        int deltaX = targetLocation.x() - previousTargetLocation.x();
        int deltaY = targetLocation.y() - previousTargetLocation.y();

        previousTargetLocation = targetLocation;

        // Mirror the movement (opposite direction)
        if (deltaX != 0 || deltaY != 0) {
            int mirrorDeltaX = -deltaX;
            int mirrorDeltaY = -deltaY;

            // Find exit that matches the mirrored direction
            for (Exit exit : location.getExits()) {
                Location dest = exit.getDestination();
                int exitDeltaX = dest.x() - location.x();
                int exitDeltaY = dest.y() - location.y();

                if (exitDeltaX == mirrorDeltaX && exitDeltaY == mirrorDeltaY) {
                    if (dest.canActorEnter(actor)) {
                        return dest.getMoveAction(actor, "mirroring " + target, exit.getHotKey());
                    }
                }
            }
        }

        // If can't mirror (blocked), just wander
        return getRandomMoveAction(actor, location);
    }

    private Action getRandomMoveAction(Actor actor, Location location) {
        List<Action> validMoves = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().canActorEnter(actor)) {
                validMoves.add(exit.getDestination().getMoveAction(actor, "around", exit.getHotKey()));
            }
        }
        if (!validMoves.isEmpty()) {
            return validMoves.get(0);
        }
        return null;
    }
}