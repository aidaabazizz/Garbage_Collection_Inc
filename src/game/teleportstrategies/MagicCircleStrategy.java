package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Flask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is a ground type where magic circle teleports worker to a random magic
 * circle on the same map and spawns a Flask on arrival.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class MagicCircleStrategy extends BaseTeleportStrategy {

    public MagicCircleStrategy() {
        super("Magic Circle");
    }

    @Override
    protected Location determineActualDestination(Location target) {
        // Grab the active map and the actor standing at this position
        GameMap map = target.map();
        Actor actor = target.getActor();

        List<Location> validCircles = new ArrayList<>();

        // If no actor is present for some reason, fallback to target
        if (actor == null) {
            return target;
        }

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);

                // Ensure target circle doesn't already have an actor standing on it
                if (loc.getGround().getDisplayChar() == '◎' && !loc.equals(target)) {
                    if (!loc.containsAnActor()) {
                        validCircles.add(loc);
                    }
                }
            }
        }

        // Safe fallback: If all other circles are occupied, stay safely where you are
        if (validCircles.isEmpty()) {
            return target;
        }

        Random rand = new Random();
        return validCircles.get(rand.nextInt(validCircles.size()));
    }

    @Override
    public void applySourceEffects(Location source) {
        // Magic circles do not destroy or change the ground layout at the source on departure
    }


    @Override
    public void applyDestinationEffects(Location destination) {
        for (Location adjacent : destination.getNearbyLocations(1)) {
            if (!adjacent.containsAnActor() && adjacent.getGround().canActorEnter(null)) {
                adjacent.addItem(new Flask());
                break;
            }
        }
    }

    @Override
    public String getActionDescription(Actor actor) {
        return actor + " travels to " + getDestinationName() + " using Magic Circle";
    }
}