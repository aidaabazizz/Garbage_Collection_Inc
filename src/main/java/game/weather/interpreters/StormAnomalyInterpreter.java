package game.weather.interpreters;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weather.WeatherSnapshot;

/**
 * Interprets storm and wind conditions as a storm surge anomaly.
 * <p>
 * Storm surge strengthens atmospheric charge from REQ3 and distortion effects
 * from REQ4.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class StormAnomalyInterpreter implements WeatherAnomalyInterpreter {

    private static final double WIND_THRESHOLD = 8.0;

    /**
     * Checks whether the snapshot indicates storm surge conditions.
     *
     * @param snapshot the weather snapshot
     * @return true if wind or storm conditions should activate storm surge
     */
    @Override
    public boolean canInterpret(WeatherSnapshot snapshot) {
        return snapshot.getWindSpeed() >= WIND_THRESHOLD
                || snapshot.conditionContains("storm")
                || snapshot.conditionContains("thunderstorm");
    }

    /**
     * Describes the storm surge anomaly.
     *
     * @param snapshot the weather snapshot
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @return a description of the storm surge anomaly
     */
    @Override
    public String interpret(WeatherSnapshot snapshot, Actor actor, GameMap map) {
        return "A storm surge rolls through the facility. Atmospheric charge and distortion intensify.";
    }
}