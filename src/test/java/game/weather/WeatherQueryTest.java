package game.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for WeatherQuery in REQ5.
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherQueryTest {

    /**
     * Tests that WeatherQuery builds a URL using dynamic query values.
     */
    @Test
    public void weatherQueryBuildsDynamicUrl() {
        WeatherQuery query = new WeatherQuery(-27.4705, 153.0260, "metric");

        String url = query.buildUrl("test_key");

        assertTrue(url.contains("lat=-27.4705"));
        assertTrue(url.contains("lon=153.026"));
        assertTrue(url.contains("appid=test_key"));
        assertTrue(url.contains("units=metric"));
    }
}