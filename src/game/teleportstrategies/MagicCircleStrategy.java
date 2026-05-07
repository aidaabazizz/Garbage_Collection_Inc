package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;
import game.items.Flask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MagicCircleStrategy implements TeleportStrategy {
    private final static int ADJACENT_TILE = 1;
    private final Random random = new Random();

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

    @Override
    public String menuDescription(Actor actor) {
        return "Teleport through the Magic Circle.";
    }

}
