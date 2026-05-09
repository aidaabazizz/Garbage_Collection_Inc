package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.TeleportStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseTeleportStrategy implements TeleportStrategy {
    protected final Random random = new Random();

    /**
     * Finds a random valid location.
     * Unbiased approach: Scans once for valid spots to avoid infinite loops
     * while maintaining randomness. (Adheres to LO4 Robustness).
     */
    protected Location getRandomValidLocation(GameMap map, Actor actor) {
        List<Location> validLocations = new ArrayList<>();

        // Collect all possible valid spots
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.canActorEnter(actor) && !loc.containsAnActor()) {
                    validLocations.add(loc);
                }
            }
        }

        // If no spots exist, return null (TeleportAction will handle this)
        if (validLocations.isEmpty()) {
            return null;
        }

        // Pick one randomly from the list of guaranteed valid spots
        return validLocations.get(random.nextInt(validLocations.size()));
    }

    @Override
    public void applySideEffects(Actor actor, Location source, Location destination, GameMap map) {}
}