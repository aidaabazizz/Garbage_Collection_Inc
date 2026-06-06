package game.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for WeatherSnapshot in REQ5.
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherSnapshotTest {

    /**
     * Tests that WeatherSnapshot stores weather values correctly.
     */
    @Test
    public void weatherSnapshotStoresWeatherValues() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                28.5,
                82,
                9.1,
                "Rain",
                "Brisbane",
                "AU"
        );

        assertEquals(28.5, snapshot.getTemperature());
        assertEquals(82, snapshot.getHumidity());
        assertEquals(9.1, snapshot.getWindSpeed());
        assertEquals("Rain", snapshot.getCondition());
        assertEquals("Brisbane", snapshot.getCityName());
        assertEquals("AU", snapshot.getCountryCode());
    }

    /**
     * Tests that conditionContains ignores case.
     */
    @Test
    public void conditionContainsIgnoresCase() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                25.0,
                60,
                3.0,
                "Thunderstorm",
                "Brisbane",
                "AU"
        );

        assertTrue(snapshot.conditionContains("storm"));
    }

    /**
     * Tests that WeatherSnapshot returns city and country together.
     */
    @Test
    public void sourceLocationCombinesCityAndCountry() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                25.0,
                60,
                3.0,
                "Clear",
                "Brisbane",
                "AU"
        );

        assertEquals("Brisbane, AU", snapshot.getSourceLocation());
    }
}