package game.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Client responsible for calling the external weather API.
 * <p>
 * This class only handles communication with the API and conversion of the
 * response into a WeatherSnapshot. It does not apply any game effects.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherApiClient {

    private final WeatherConfig config;
    private final HttpClient httpClient;

    /**
     * Constructor for WeatherApiClient.
     *
     * @param config the weather configuration provider
     */
    public WeatherApiClient(WeatherConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Fetches weather data using the given query.
     *
     * @param query the weather query
     * @return the parsed weather snapshot
     * @throws WeatherApiException if the API request or parsing fails
     */
    public WeatherSnapshot fetchWeather(WeatherQuery query) throws WeatherApiException {
        try {
            String apiKey = config.getApiKey();
            String url = query.buildUrl(apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new WeatherApiException(
                        "Weather API returned status code: " + response.statusCode()
                );
            }

            return parseSnapshot(response.body());

        } catch (IOException exception) {
            throw new WeatherApiException("Failed to call weather API.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new WeatherApiException("Weather API request was interrupted.", exception);
        }
    }

    /**
     * Parses the API response body into a WeatherSnapshot.
     * <p>
     * This is intentionally kept private so other classes do not depend on raw
     * JSON. If the parser grows later, it can be extracted into WeatherJsonParser.
     * </p>
     *
     * @param body the raw JSON response body
     * @return the parsed weather snapshot
     * @throws WeatherApiException if the expected fields are missing
     */
    private WeatherSnapshot parseSnapshot(String body) throws WeatherApiException {
        double temperature = extractDouble(body, "\"temp\":");
        int humidity = (int) extractDouble(body, "\"humidity\":");
        double windSpeed = extractDouble(body, "\"speed\":");
        String condition = extractString(body, "\"main\":\"");
        String cityName = extractString(body, "\"name\":\"");
        String countryCode = extractString(body, "\"country\":\"");

        return new WeatherSnapshot(
                temperature,
                humidity,
                windSpeed,
                condition,
                cityName,
                countryCode
        );
    }

    /**
     * Extracts a double value after the given JSON marker.
     *
     * @param body the JSON body
     * @param marker the JSON field marker
     * @return the parsed double value
     * @throws WeatherApiException if the value cannot be found
     */
    private double extractDouble(String body, String marker) throws WeatherApiException {
        int start = body.indexOf(marker);

        if (start < 0) {
            throw new WeatherApiException("Missing JSON marker: " + marker);
        }

        start += marker.length();
        int end = start;

        while (end < body.length() && isNumberCharacter(body.charAt(end))) {
            end++;
        }

        return Double.parseDouble(body.substring(start, end));
    }

    /**
     * Extracts a string value after the given JSON marker.
     *
     * @param body the JSON body
     * @param marker the JSON field marker
     * @return the parsed string value
     * @throws WeatherApiException if the value cannot be found
     */
    private String extractString(String body, String marker) throws WeatherApiException {
        int start = body.indexOf(marker);

        if (start < 0) {
            throw new WeatherApiException("Missing JSON marker: " + marker);
        }

        start += marker.length();
        int end = body.indexOf("\"", start);

        if (end < 0) {
            throw new WeatherApiException("Unclosed JSON string for marker: " + marker);
        }

        return body.substring(start, end);
    }

    /**
     * Checks whether a character is part of a JSON numeric value.
     *
     * @param character the character to check
     * @return true if the character is numeric syntax
     */
    private boolean isNumberCharacter(char character) {
        return Character.isDigit(character)
                || character == '.'
                || character == '-';
    }
}