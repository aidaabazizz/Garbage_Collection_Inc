package game.weather;

/**
 * Immutable data object representing weather values returned by the external API.
 * <p>
 * This class prevents the rest of the game from depending directly on raw JSON
 * or API-specific response structures. Game systems can read weather values
 * through clear getter methods.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherSnapshot {

    private final double temperature;
    private final int humidity;
    private final double windSpeed;
    private final String condition;

    /**
     * Constructor for WeatherSnapshot.
     *
     * @param temperature the current temperature in Celsius
     * @param humidity the current humidity percentage
     * @param windSpeed the current wind speed
     * @param condition the main weather condition, e.g. Rain, Clear, Thunderstorm
     */
    public WeatherSnapshot(double temperature, int humidity, double windSpeed, String condition) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
    }

    /**
     * Gets the current temperature.
     *
     * @return the temperature in Celsius
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Gets the current humidity.
     *
     * @return the humidity percentage
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     * Gets the current wind speed.
     *
     * @return the wind speed
     */
    public double getWindSpeed() {
        return windSpeed;
    }

    /**
     * Gets the weather condition.
     *
     * @return the main weather condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Checks whether the weather condition contains a keyword.
     *
     * @param keyword the keyword to check
     * @return true if the condition contains the keyword, ignoring case
     */
    public boolean conditionContains(String keyword) {
        return condition != null && condition.toLowerCase().contains(keyword.toLowerCase());
    }
}