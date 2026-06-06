package game.weather.interpreters;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weather.WeatherSnapshot;

/**
 * Interprets humidity and rain as conductive weather conditions.
 * <p>
 * High humidity and rain are used to strengthen the electricity and magnetism
 * systems from REQ3.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class HumidityAnomalyInterpreter implements WeatherAnomalyInterpreter {

    private static final int HUMIDITY_THRESHOLD = 75;

    /**
     * Checks whether the snapshot indicates conductive rain conditions.
     *
     * @param snapshot the weather snapshot
     * @return true if humidity or rain should activate conductive anomalies
     */
    @Override
    public boolean canInterpret(WeatherSnapshot snapshot) {
        return snapshot.getHumidity() >= HUMIDITY_THRESHOLD
                || snapshot.conditionContains("rain");
    }

    /**
     * Describes the conductive rain anomaly.
     *
     * @param snapshot the weather snapshot
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @return a description of the conductive anomaly
     */
    @Override
    public String interpret(WeatherSnapshot snapshot, Actor actor, GameMap map) {
        return "The air is heavy with moisture. Conductive moisture strengthens electrical anomalies.";
    }
}