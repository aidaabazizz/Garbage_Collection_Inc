package game.weather.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.WeatherSnapshot;

/**
 * Represents a weather-driven effect that changes the game world.
 * <p>
 * Implementations must create meaningful game-state impact, such as changing
 * terrain, altering existing anomaly systems, moving actors, or applying statuses.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public interface AnomalyWorldEffect {

    /**
     * Checks whether this effect should apply to the given weather snapshot.
     *
     * @param snapshot the weather snapshot
     * @return true if the effect should be applied
     */
    boolean canApply(WeatherSnapshot snapshot);

    /**
     * Applies the weather effect to the current game world.
     *
     * @param actor the actor triggering the effect
     * @param map the current game map
     * @param location the actor's current location
     * @param snapshot the weather snapshot
     * @return a description of what happened
     */
    String applyEffect(Actor actor, GameMap map, Location location, WeatherSnapshot snapshot);
}