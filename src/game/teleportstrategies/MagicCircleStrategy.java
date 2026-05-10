package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;
import game.items.Flask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is a ground type where magic circle teleports worker to a random magic
 * circle on the same map and spawns a Flask on arrival.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class MagicCircleStrategy implements TeleportStrategy {
    /** Radius for adjacent tile search */
    private final static int ADJACENT_TILE = 1;

    /** Random destination selector*/
    private final Random random = new Random();

    /**
     * This will find and return a random destination magic circle.
     * @param actor the actor teleporting
     * @param map the current game map
     * @return random magic circle location or current location if none exist
     */
    @Override
    public Location getDestination(Actor actor, GameMap map) {
        List<Location> otherCircles = new ArrayList<>();
        Location currentLocation = map.locationOf(actor);

        for (int x: map.getXRange()) {
            for (int y: map.getYRange()) {
                Location loc = map.at(x,y);
                if (loc.getGround().getDisplayChar() == '◎' && !loc.equals(currentLocation)) {
                    otherCircles.add(loc);
                }
            }
        }
        if (otherCircles.isEmpty()) return currentLocation;
        return otherCircles.get(random.nextInt(otherCircles.size()));
    }

    /**
     * This will spawn a flask on an empty adjacent tile at the destination
     * @param actor the actor teleporting
     * @param source the source location
     * @param destination the destination location
     * @param map the current game map
     */
    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {
        for (Location adjacent: destination.getNearbyLocations(ADJACENT_TILE)) {
            if (!adjacent.containsAnActor() && adjacent.canActorEnter(actor)) {
                adjacent.addItem(new Flask());
                // this ensures that only one Flask will be added
                break;
            }
        }
    }

    /**
     * Returns the menu description of magic circle teleportation.
     * @param actor the actor teleporting
     * @return menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Teleport through the Magic Circle.";
    }

}
