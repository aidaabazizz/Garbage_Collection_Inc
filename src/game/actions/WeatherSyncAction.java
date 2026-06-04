package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.WeatherAnomalyManager;
import game.weather.WeatherApiClient;
import game.weather.WeatherApiException;
import game.weather.WeatherQuery;
import game.weather.WeatherSnapshot;
import game.enums.WeatherMapAnchor;
/**
 * Action that synchronises the game world with real-world weather data.
 * <p>
 * This action is provided by the SuperComputer. It builds a dynamic API request
 * using the actor's current map and location, then applies anomaly effects based
 * on the returned weather data.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherSyncAction extends Action {

    private static final String UNITS = "metric";

    private final WeatherApiClient apiClient;
    private final WeatherAnomalyManager manager;

    /**
     * Constructor for WeatherSyncAction.
     *
     * @param apiClient the weather API client
     * @param manager the anomaly manager
     */
    public WeatherSyncAction(WeatherApiClient apiClient, WeatherAnomalyManager manager) {
        this.apiClient = apiClient;
        this.manager = manager;
    }

    /**
     * Executes the weather sync.
     *
     * @param actor the actor performing the action
     * @param map the current map
     * @return a description of the weather sync result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        try {
            WeatherQuery query = createQuery(actor, map);
            WeatherSnapshot snapshot = apiClient.fetchWeather(query);
            return manager.applyWeather(snapshot, actor, map);
        } catch (WeatherApiException exception) {
            return "Weather sync failed: " + exception.getMessage();
        }
    }

    /**
     * Provides the menu description for this action.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " syncs the SuperComputer with real-world weather anomalies";
    }

    /**
     * Builds a dynamic weather query using the actor's current game state.
     *
     * @param actor the actor triggering the sync
     * @param map the current map
     * @return the dynamic weather query
     */
    private WeatherQuery createQuery(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        WeatherMapAnchor anchor = WeatherMapAnchor.fromMapName(map.toString());

        double latitude = anchor.getLatitude() + location.x() * 0.001;
        double longitude = anchor.getLongitude() + location.y() * 0.001;

        return new WeatherQuery(latitude, longitude, UNITS);
    }
}