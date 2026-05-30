package game.weather;
import game.weather.WeatherApiException;

/**
 * Provides API configuration for the weather system.
 * <p>
 * The API key is read from an environment variable so that sensitive keys are
 * not committed to the GitLab repository.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherConfig {

    private static final String API_KEY_ENV_NAME = "OPENWEATHER_API_KEY";

    /**
     * Gets the OpenWeather API key from the environment.
     *
     * @return the API key
     * @throws WeatherApiException if the API key is missing
     */
    public String getApiKey() throws WeatherApiException {
        String apiKey = System.getenv(API_KEY_ENV_NAME);

        if (apiKey == null || apiKey.isBlank()) {
            throw new WeatherApiException(
                    "Missing environment variable: " + API_KEY_ENV_NAME
            );
        }

        return apiKey;
    }
}