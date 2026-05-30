package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import java.util.Random;
/**
 * Teleportation Tube strategy.
 * It will teleport worker to fixed destinations with 50% malfunction chancs.
 * The effect after teleportation is burning adjacent tiles on arrival.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class TeleportTubeStrategy extends BaseTeleportStrategy {
    private final Random random = new Random();

    public TeleportTubeStrategy(Location targetLocation, String destinationName) {
        super(targetLocation, destinationName);
    }

    @Override
    protected Location determineActualDestination(Location target) {
        if (random.nextDouble() < 0.50) {
            Actor actor = target.getActor();
            Location random = findRandomValidLocation(target.map(), actor);
            if (random != null) return random;
        }
        return target;
    }

    @Override
    protected void applySourceEffects(Location source) {}

    @Override
    protected void applyDestinationEffects(Location destination) {
        // Burns the surroundings of the destination location
        for (Exit exit : destination.getExits()) {
            Location adjLocation = exit.getDestination();
            char groundChar = adjLocation.getGround().getDisplayChar();

            if (groundChar == '_') {
                Ground groundBeforeFire = adjLocation.getGround();
                adjLocation.setGround(new Fire(groundBeforeFire));
            }
        }
    }

    @Override
    public String getActionDescription(Actor actor) {
        return actor + " travels to " + getDestinationName() + " using Teleportation Tube";
    }
}