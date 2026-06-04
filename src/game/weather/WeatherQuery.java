package game.weather;

/**
 * Represents the dynamic API query used to request real-world weather data.
 * <p>
 * The query is built from game-state variables, such as the actor's current
 * map and coordinates. This prevents the API request from being static.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherQuery {

    private final double latitude;
    private final double longitude;
    private final String units;

    /**
     * Constructor for WeatherQuery.
     *
     * @param latitude the latitude used in the API request
     * @param longitude the longitude used in the API request
     * @param units the unit system, e.g. metric
     */
    public WeatherQuery(double latitude, double longitude, String units) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.units = units;
    }

    /**
     * Builds the query string used by the weather API.
     *
     * @param apiKey the API key
     * @return the complete API URL
     */
    public String buildUrl(String apiKey) {
        return "https://api.openweathermap.org/data/2.5/weather"
                + "?lat=" + latitude
                + "&lon=" + longitude
                + "&appid=" + apiKey
                + "&units=" + units;
    }

    /**
     * Gets the query latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the query longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the unit system.
     *
     * @return the unit system
     */
    public String getUnits() {
        return units;
    }
}