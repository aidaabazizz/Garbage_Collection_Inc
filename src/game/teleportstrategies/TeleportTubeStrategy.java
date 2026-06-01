package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;

/**
 * Teleportation Tube strategy.
 * It will teleport worker to fixed destinations with 50% malfunction chance.
 * The effect after teleportation is burning adjacent tiles on arrival.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class TeleportTubeStrategy extends BaseTeleportStrategy {
    /** A flag to track whether teleport tube malfunctioned when teleporting actor **/
    private boolean malfunctioned = false;

    /**
     * Constructs a TeleportTubeStrategy with a specified destination.
     *
     * @param targetLocation the intended destination location
     * @param destinationName the name of the destination displayed to the player
     */
    public TeleportTubeStrategy(Location targetLocation, String destinationName) {
        super(targetLocation, destinationName);
    }

    /**
     * Determines the actor's actual destination.
     * There is a 50% chance that the teleportation tube malfunctions,
     * causing the actor to be sent to a random valid location on the same map.
     * Otherwise, the actor arrives at the intended destination.
     *
     * @param target the intended destination location
     * @return the actual destination location after malfunction checks
     */
    @Override
    protected Location determineActualDestination(Location target) {
        if (RANDOM.nextDouble() < 0.50) {
            Location random = findRandomValidLocation(target.map(), getTeleportingActor());
            if (random != null) {
                malfunctioned = true;
                return random;
            }
        }
        malfunctioned = false;
        return target;
    }

    /**
     * Applies any effects at the source location before teleportation.
     * Teleportation Tubes do not apply any source-side effects.
     *
     * @param source the location the actor is teleporting from
     */
    @Override
    protected void applySourceEffects(Location source) {}

    /**
     * Applies arrival effects at the destination location.
     * All adjacent locations containing burnable ground
     * are replaced with a Fire ground instance.
     *
     * @param destination the location the actor arrives at
     */
    @Override
    protected void applyDestinationEffects(Location destination) {
        for (Exit exit : destination.getExits()) {
            Location adjLocation = exit.getDestination();
            char groundChar = adjLocation.getGround().getDisplayChar();
            if (groundChar == '_') {
                adjLocation.setGround(new Fire(adjLocation.getGround()));
            }
        }
    }

    /**
     * Returns a description of the teleportation action for display to the player.
     *
     * @param actor the actor performing the teleportation
     * @return a string describing the teleportation action
     */
    @Override
    public String getActionDescription(Actor actor) {
        if (malfunctioned) {
            return actor + " attempts to travel to " + getDestinationName()
                    + " using Teleportation Tube, but it malfunctions! "
                    + actor + " is sent to a random location!";
        }
        return actor + " travels to " + getDestinationName() + " using Teleportation Tube";
    }
}