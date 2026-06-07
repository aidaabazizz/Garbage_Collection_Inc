package game.enums;

import java.util.Arrays;

/**
 * Represents the real-world weather anchor linked to each game map.
 * <p>
 * Each anchor stores a map name keyword and base coordinates. The weather
 * system uses these coordinates to build a dynamic API query from the actor's
 * current map and position.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public enum WeatherMapAnchor {
    DEPRECATED_MOON("99-Deprecated", -27.4705, 153.0260),
    OVERFLOW_MOON("20-overflow", -37.8136, 144.9631),
    DEFAULT("default", -27.4705, 153.0260);

    private final String mapKeyword;
    private final double latitude;
    private final double longitude;

    /**
     * Constructor for WeatherMapAnchor.
     *
     * @param mapKeyword the keyword used to match the game map name
     * @param latitude the base latitude for the weather API query
     * @param longitude the base longitude for the weather API query
     */
    WeatherMapAnchor(String mapKeyword, double latitude, double longitude) {
        this.mapKeyword = mapKeyword;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Finds the matching weather anchor for a map name.
     * <p>
     * This avoids switch statements by letting each enum value decide whether it
     * matches the provided map name.
     * </p>
     *
     * @param mapName the current game map name
     * @return the matching weather anchor, or DEFAULT if no anchor matches
     */
    public static WeatherMapAnchor fromMapName(String mapName) {
        return Arrays.stream(values())
                .filter(anchor -> anchor.matches(mapName))
                .findFirst()
                .orElse(DEFAULT);
    }

    /**
     * Checks whether this weather anchor matches the provided map name.
     *
     * @param mapName the current game map name
     * @return true if the map name contains this anchor's keyword
     */
    private boolean matches(String mapName) {
        return mapName != null
                && this != DEFAULT
                && mapName.toLowerCase().contains(mapKeyword.toLowerCase());
    }

    /**
     * Gets the base latitude.
     *
     * @return the base latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the base longitude.
     *
     * @return the base longitude
     */
    public double getLongitude() {
        return longitude;
    }
}