package game.weather.interpreters;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.weather.WeatherSnapshot;

/**
 * Interprets high temperature as heat distortion.
 * <p>
 * High temperature is used to destabilise blue fire, safehouses, and spatial
 * distortion systems from REQ4.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class TemperatureAnomalyInterpreter implements WeatherAnomalyInterpreter {

    private static final double HEAT_THRESHOLD = 32.0;

    /**
     * Checks whether the snapshot indicates heat distortion conditions.
     *
     * @param snapshot the weather snapshot
     * @return true if temperature should activate heat distortion
     */
    @Override
    public boolean canInterpret(WeatherSnapshot snapshot) {
        return snapshot.getTemperature() >= HEAT_THRESHOLD;
    }

    /**
     * Describes the heat distortion anomaly.
     *
     * @param snapshot the weather snapshot
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @return a description of the heat anomaly
     */
    @Override
    public String interpret(WeatherSnapshot snapshot, Actor actor, GameMap map) {
        return "The facility overheats. Heat distortion destabilises sanctuary systems.";
    }
}