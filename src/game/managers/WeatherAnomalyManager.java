package game.managers;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.weather.WeatherSnapshot;
import game.weather.effects.AnomalyWorldEffect;
import game.weather.interpreters.WeatherAnomalyInterpreter;

import java.util.List;

/**
 * Coordinates interpretation and application of weather anomaly effects.
 * <p>
 * This class depends on abstractions instead of concrete effect classes. This
 * supports dependency inversion and makes the system open for extension.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherAnomalyManager {

    private final List<WeatherAnomalyInterpreter> interpreters;
    private final List<AnomalyWorldEffect> effects;

    /**
     * Constructor for WeatherAnomalyManager.
     *
     * @param interpreters weather anomaly interpreters
     * @param effects weather world effects
     */
    public WeatherAnomalyManager(
            List<WeatherAnomalyInterpreter> interpreters,
            List<AnomalyWorldEffect> effects
    ) {
        this.interpreters = List.copyOf(interpreters);
        this.effects = List.copyOf(effects);
    }

    /**
     * Applies weather anomalies to the current game world.
     *
     * @param snapshot the weather snapshot
     * @param actor the actor triggering the sync
     * @param map the current game map
     * @return a description of all interpreted and applied effects
     */
    public String applyWeather(WeatherSnapshot snapshot, Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        StringBuilder result = new StringBuilder();

        result.append("Weather synced from ")
                .append(snapshot.getSourceLocation())
                .append(": ")
                .append(snapshot.getTemperature())
                .append("°C, ")
                .append(snapshot.getHumidity())
                .append("% humidity, ")
                .append(snapshot.getWindSpeed())
                .append(" wind speed, ")
                .append(snapshot.getCondition())
                .append(".")
                .append("\n");

        boolean anomalyDetected = false;

        for (WeatherAnomalyInterpreter interpreter : interpreters) {
            if (interpreter.canInterpret(snapshot)) {
                result.append(interpreter.interpret(snapshot, actor, map)).append("\n");
                anomalyDetected = true;
            }
        }

        for (AnomalyWorldEffect effect : effects) {
            if (effect.canApply(snapshot)) {
                result.append(effect.applyEffect(actor, map, location, snapshot)).append("\n");
                anomalyDetected = true;
            }
        }

        if (!anomalyDetected) {
            result.append("No anomaly conditions were detected.");
        }

        return result.toString();
    }
}