package game.weather;

/**
 * Exception used when the weather API cannot be accessed or parsed.
 * <p>
 * This gives the weather system a clear failure type instead of hiding API
 * failures inside generic runtime errors.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherApiException extends Exception {

    /**
     * Constructor for WeatherApiException.
     *
     * @param message the error message
     */
    public WeatherApiException(String message) {
        super(message);
    }

    /**
     * Constructor for WeatherApiException with cause.
     *
     * @param message the error message
     * @param cause the original exception
     */
    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}