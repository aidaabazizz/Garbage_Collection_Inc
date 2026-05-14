package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Map;

/**
 * Movement action that sends the player in the opposite direction when disoriented.
 * Menu shows normal movement options - player doesn't know they will be disoriented.
 *
 * @author Aida
 * @version 1.0
 */
public class DisorientedMoveAction extends Action {
    private final String intendedDirection;
    private final String hotKey;

    public DisorientedMoveAction(String direction, String hotKey) {
        this.intendedDirection = direction;
        this.hotKey = hotKey;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        // Always go opposite direction
        String actualDirection = getOppositeDirection(intendedDirection);
        return executeMove(actor, map, actualDirection);
    }

    /**
     * Returns the opposite direction.
     */
    private static final Map<String, String> OPPOSITES = Map.of(
            "North", "South",
            "South", "North",
            "East", "West",
            "West", "East"
    );

    private String getOppositeDirection(String direction) {
        return OPPOSITES.getOrDefault(direction, direction);
    }

    private String executeMove(Actor actor, GameMap map, String direction) {
        Location current = map.locationOf(actor);
        Location destination = getDestination(current, direction, map);

        if (destination != null && destination.canActorEnter(actor)) {
            map.moveActor(actor, destination);
            // Show the truth AFTER moving
            return String.format("\u001B[36m%s tried to go %s but was disoriented and went %s instead!\u001B[0m",
                    actor, intendedDirection, direction);
        }

        return String.format("%s cannot move %s", actor, direction);
    }

    private Location getDestination(Location current, String direction, GameMap map) {
        int newX = current.x();
        int newY = current.y();

        if (direction.equals("North")) newY--;
        else if (direction.equals("South")) newY++;
        else if (direction.equals("East")) newX++;
        else if (direction.equals("West")) newX--;
        else return null;

        if (map.getXRange().contains(newX) && map.getYRange().contains(newY)) {
            return map.at(newX, newY);
        }
        return null;
    }

    @Override
    public String menuDescription(Actor actor) {
        // Normal menu display - player doesn't know they will be disoriented!
        String hotKeyDisplay = hotKey != null && !hotKey.isEmpty() ? " (" + hotKey + ")" : "";
        return actor + " moves " + intendedDirection + hotKeyDisplay;
    }
}