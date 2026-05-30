package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Flask;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a ground type where magic circle teleports worker to a random magic
 * circle on the same map and spawns a Flask on arrival.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class MagicCircleStrategy extends BaseTeleportStrategy {

    /**
     * Constructs a MagicCircleStrategy with the default destination name
     * "Magic Circle".
     */
    public MagicCircleStrategy() {
        super("Magic Circle");
    }

    /**
     * Determines the destination of the teleportation.
     * Searches the current map for all unoccupied Magic Circles other than
     * the source Magic Circle and randomly selects one as the destination.
     * If no valid destination exists, the source location is returned.
     *
     * @param target the Magic Circle being used as the source location
     * @return a randomly selected valid Magic Circle destination, or the
     *         source location if no valid destination exists
     */
    @Override
    protected Location determineActualDestination(Location target) {
        GameMap map = target.map();
        List<Location> validCircles = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround().getDisplayChar() == '◎'
                        && !loc.equals(target)
                        && !loc.containsAnActor()) {
                    validCircles.add(loc);
                }
            }
        }

        if (validCircles.isEmpty()) return target;
        return validCircles.get(RANDOM.nextInt(validCircles.size()));
    }

    /**
     * Applies any effects at the source location before teleportation.
     * Magic Circles do not produce any departure effects and leave the
     * source location unchanged.
     *
     * @param source the location the actor is teleporting from
     */
    @Override
    public void applySourceEffects(Location source) {
    }

    /**
     * Applies arrival effects at the destination location.
     * Attempts to place a Flask in the first adjacent location that is
     * unoccupied and can be entered. If no such location exists, no Flask
     * is spawned.
     *
     * @param destination the location where the actor arrives
     */
    @Override
    public void applyDestinationEffects(Location destination) {
        for (Location adjacent : destination.getNearbyLocations(1)) {
            if (!adjacent.equals(destination)
                    && !adjacent.containsAnActor()
                    && adjacent.getGround().canActorEnter(getTeleportingActor())) {
                adjacent.addItem(new Flask());
                break;
            }
        }
    }

    /**
     * Returns a description of the teleportation action.
     *
     * @param actor the actor performing the teleportation
     * @return a string describing the teleportation action
     */
    @Override
    public String getActionDescription(Actor actor) {
        return actor + " travels to " + getDestinationName() + " using Magic Circle";
    }
}