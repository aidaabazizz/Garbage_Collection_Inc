package game.weather.interpreters;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weather.WeatherSnapshot;

/**
 * Represents an object that interprets weather API data into an anomaly meaning.
 * <p>
 * Interpreters do not directly modify the game world. They decide whether the
 * weather snapshot contains a meaningful condition for the anomaly system.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public interface WeatherAnomalyInterpreter {

    /**
     * Checks whether this interpreter can interpret the given snapshot.
     *
     * @param snapshot the weather snapshot
     * @return true if this interpreter should respond to the snapshot
     */
    boolean canInterpret(WeatherSnapshot snapshot);

    /**
     * Produces a descriptive interpretation of the weather snapshot.
     *
     * @param snapshot the weather snapshot
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @return a description of the interpreted anomaly
     */
    String interpret(WeatherSnapshot snapshot, Actor actor, GameMap map);
}