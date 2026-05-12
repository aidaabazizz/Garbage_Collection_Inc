package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.DisorientedCapability;

import java.util.Random;

/**
 * Movement action that randomizes direction when actor is disoriented.
 * Uses capability pattern - NO instanceof, NO switch!
 *
 * @author Aida
 */
public class DisorientedMoveAction extends Action {
    private final String intendedDirection;
    private final String hotKey;
    private final Random random = new Random();
    private static final String[] DIRECTIONS = {"North", "South", "East", "West"};

    public DisorientedMoveAction(String direction, String hotKey) {
        this.intendedDirection = direction;
        this.hotKey = hotKey;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        boolean isDisoriented = actor.asCapability(DisorientedCapability.class)
                .map(DisorientedCapability::isDisoriented)
                .orElse(false);

        String actualDirection = intendedDirection;

        if (isDisoriented) {
            // Random direction using array
            actualDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        }

        return executeMove(actor, map, actualDirection);
    }

    private String executeMove(Actor actor, GameMap map, String direction) {
        Location current = map.locationOf(actor);
        Location destination = getDestination(current, direction, map);

        if (destination != null && destination.canActorEnter(actor)) {
            map.moveActor(actor, destination);
            if (!direction.equals(intendedDirection)) {
                return String.format("%s tried to go %s but stumbled %s due to blizzard!",
                        actor, intendedDirection, direction);
            }
            return String.format("%s moves %s", actor, direction);
        }

        return String.format("%s cannot move %s", actor, direction);
    }

    private Location getDestination(Location current, String direction, GameMap map) {
        // Calculate destination using arithmetic
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

    // Keep hotKey and use it in menuDescription
    @Override
    public String menuDescription(Actor actor) {
        String hotKeyDisplay = hotKey != null && !hotKey.isEmpty() ? " (" + hotKey + ")" : "";
        return actor + " attempts to go " + intendedDirection + hotKeyDisplay + " (blizzard disorients!)";
    }
}