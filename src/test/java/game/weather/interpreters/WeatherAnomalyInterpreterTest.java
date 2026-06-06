package game.weather.interpreters;

import game.weather.WeatherSnapshot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for REQ5 weather anomaly interpreters.
 *
 * @author Suchir
 * @version 1.0
 */
public class WeatherAnomalyInterpreterTest {

    /**
     * Tests that high humidity activates the humidity interpreter.
     */
    @Test
    public void humidityInterpreterDetectsHighHumidity() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                25.0,
                80,
                2.0,
                "Clear",
                "Brisbane",
                "AU"
        );

        HumidityAnomalyInterpreter interpreter = new HumidityAnomalyInterpreter();

        assertTrue(interpreter.canInterpret(snapshot));
    }

    /**
     * Tests that rain condition activates the humidity interpreter.
     */
    @Test
    public void humidityInterpreterDetectsRainCondition() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                25.0,
                40,
                2.0,
                "Rain",
                "Brisbane",
                "AU"
        );

        HumidityAnomalyInterpreter interpreter = new HumidityAnomalyInterpreter();

        assertTrue(interpreter.canInterpret(snapshot));
    }

    /**
     * Tests that high temperature activates the temperature interpreter.
     */
    @Test
    public void temperatureInterpreterDetectsHighTemperature() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                35.0,
                40,
                2.0,
                "Clear",
                "Brisbane",
                "AU"
        );

        TemperatureAnomalyInterpreter interpreter = new TemperatureAnomalyInterpreter();

        assertTrue(interpreter.canInterpret(snapshot));
    }

    /**
     * Tests that high wind activates the storm interpreter.
     */
    @Test
    public void stormInterpreterDetectsHighWind() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                25.0,
                40,
                9.0,
                "Clear",
                "Brisbane",
                "AU"
        );

        StormAnomalyInterpreter interpreter = new StormAnomalyInterpreter();

        assertTrue(interpreter.canInterpret(snapshot));
    }

    /**
     * Tests that thunderstorm condition activates the storm interpreter.
     */
    @Test
    public void stormInterpreterDetectsThunderstormCondition() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                25.0,
                40,
                2.0,
                "Thunderstorm",
                "Brisbane",
                "AU"
        );

        StormAnomalyInterpreter interpreter = new StormAnomalyInterpreter();

        assertTrue(interpreter.canInterpret(snapshot));
    }

    /**
     * Tests that mild weather does not activate any interpreter.
     */
    @Test
    public void interpretersIgnoreMildWeather() {
        WeatherSnapshot snapshot = new WeatherSnapshot(
                22.0,
                40,
                2.0,
                "Clear",
                "Brisbane",
                "AU"
        );

        assertFalse(new HumidityAnomalyInterpreter().canInterpret(snapshot));
        assertFalse(new TemperatureAnomalyInterpreter().canInterpret(snapshot));
        assertFalse(new StormAnomalyInterpreter().canInterpret(snapshot));
    }
}