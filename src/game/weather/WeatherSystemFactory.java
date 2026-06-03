package game.weather;

import game.actions.WeatherSyncAction;
import game.managers.WeatherAnomalyManager;
import game.weather.effects.AnomalyWorldEffect;
import game.weather.effects.ConductiveRainEffect;
//import game.weather.effects.HeatDistortionEffect;
import game.weather.effects.StormSurgeEffect;
import game.weather.interpreters.HumidityAnomalyInterpreter;
import game.weather.interpreters.StormAnomalyInterpreter;
import game.weather.interpreters.TemperatureAnomalyInterpreter;
import game.weather.interpreters.WeatherAnomalyInterpreter;

import java.util.List;

/**
 * Factory for constructing the weather anomaly system.
 * <p>
 * This keeps object creation out of SuperComputer and prevents the facility
 * class from becoming responsible for API wiring.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public final class WeatherSystemFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private WeatherSystemFactory() {
    }

    /**
     * Creates the WeatherSyncAction with all required API, interpreter, and
     * effect dependencies.
     *
     * @return a configured WeatherSyncAction
     */
    public static WeatherSyncAction createWeatherSyncAction() {
        WeatherConfig config = new WeatherConfig();
        WeatherApiClient apiClient = new WeatherApiClient(config);

        List<WeatherAnomalyInterpreter> interpreters = List.of(
                new HumidityAnomalyInterpreter(),
                new TemperatureAnomalyInterpreter(),
                new StormAnomalyInterpreter()
        );

        List<AnomalyWorldEffect> effects = List.of(
                new ConductiveRainEffect(),
                //new HeatDistortionEffect(),
                new StormSurgeEffect()
        );

        WeatherAnomalyManager manager = new WeatherAnomalyManager(
                interpreters,
                effects
        );

        return new WeatherSyncAction(apiClient, manager);
    }
}