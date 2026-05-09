package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An abstract base class that serves as a foundation for all teleportation behaviors
 * within the Eclipse Nebula. This class centralizes shared utility logic to fulfill
 * the DRY principle and provides a stable framework for
 * both Requirement 2 and Requirement 3 features.
 *
 * @author Jewell Gomes
 */
public abstract class BaseTeleportStrategy implements TeleportStrategy {
    /**
     * Random number generator used for coordinate selection and malfunction checks.
     */
    protected final Random random = new Random();

    /**
     * Identifies a random location on the specified map that is both passable and
     * currently unoccupied.
     * To ensure game stability and fulfill the robustness requirements of LO4,
     * this method scans the map to collect all valid positions rather than using
     * a random-guess loop. This prevents the application from hanging or entering
     * an infinite loop if the map is entirely occupied by creatures or objects.
     *
     * @param map   The game map to search for a destination.
     * @param actor The actor intended to be moved.
     * @return A randomly selected valid Location, or null if no valid spots exist.
     */
    protected Location getRandomValidLocation(GameMap map, Actor actor) {
        List<Location> validSpots = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.canActorEnter(actor) && !loc.containsAnActor()) {
                    validSpots.add(loc);
                }
            }
        }

        if (validSpots.isEmpty()) {
            return null;
        }
        return validSpots.get(random.nextInt(validSpots.size()));
    }

    /**
     * Executes any secondary environmental triggers that occur as a result of teleportation.
     *
     * @param actor       The actor being moved.
     * @param source      The location where the teleportation started.
     * @param destination The location where the actor arrived.
     * @param map         The map where the side effects should be applied.
     */
    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {}
}