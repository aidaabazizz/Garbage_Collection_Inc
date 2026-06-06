package game.weather.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.AtmosphericChargeSource;
import game.enums.MaterialCapability;
import game.highvoltage.ChargeContext;
import game.highvoltage.GalvanicCharge;
import game.weather.WeatherSnapshot;
import game.enums.FacilityCapability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Weather effect that turns storm and wind API data into stronger atmospheric
 * charge in the game world.
 * <p>
 * This effect connects REQ5 to the REQ3 atmospheric charge system. Instead of
 * modifying private constants inside AtmosphericChargeSource, it increases storm
 * pressure by forcing an immediate charge release and optionally placing an
 * additional charge source on a nearby safe tile.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class StormSurgeEffect implements AnomalyWorldEffect {

    private static final int RADIUS = 2;
    private static final double WIND_THRESHOLD = 8.0;
    private static final double EXTRA_SOURCE_CHANCE = 0.50;

    private final Random random;

    /**
     * Constructor for StormSurgeEffect.
     */
    public StormSurgeEffect() {
        this(new Random());
    }

    /**
     * Constructor for StormSurgeEffect with injectable randomness.
     *
     * @param random the random number generator
     */
    public StormSurgeEffect(Random random) {
        this.random = random;
    }

    /**
     * Checks whether storm surge should activate.
     *
     * @param snapshot the weather snapshot
     * @return true if wind or storm conditions should activate the effect
     */
    @Override
    public boolean canApply(WeatherSnapshot snapshot) {
        return snapshot.getWindSpeed() >= WIND_THRESHOLD
                || snapshot.conditionContains("storm")
                || snapshot.conditionContains("thunderstorm");
    }

    /**
     * Applies storm surge effects to the current game world.
     * <p>
     * A weather-amplified atmospheric charge is released immediately at the
     * actor's location. There is also a chance to place an extra
     * AtmosphericChargeSource nearby, increasing future lightning activity.
     * </p>
     *
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @param location the actor's current location
     * @param snapshot the weather snapshot
     * @return a description of the storm surge effect
     */
    @Override
    public String applyEffect(Actor actor, GameMap map, Location location, WeatherSnapshot snapshot) {
        Display display = new Display();
        ChargeContext charge = new GalvanicCharge(
                "a weather-amplified storm surge",
                display,
                3
        );

        AtmosphericChargeSource surgeSource = new AtmosphericChargeSource();
        surgeSource.releaseCharge(location, charge);

        boolean sourcePlaced = false;

        if (random.nextDouble() < EXTRA_SOURCE_CHANCE) {
            sourcePlaced = placeExtraSource(map, location, actor);
        }

        if (sourcePlaced) {
            return "Storm surge intensifies the facility: atmospheric charge erupts and a new charge source forms nearby.";
        }

        return "Storm surge intensifies the facility: atmospheric charge erupts around " + actor + ".";
    }
    /**
     * Attempts to place an extra AtmosphericChargeSource on a nearby valid tile.
     *
     * @param map the current game map
     * @param centre the centre location
     * @param actor the actor used to test passability
     * @return true if a source was placed
     */
    private boolean placeExtraSource(GameMap map, Location centre, Actor actor) {
        List<Location> candidates = nearbyLocations(map, centre);
        Collections.shuffle(candidates, random);

        for (Location candidate : candidates) {
            if (canPlaceChargeSource(candidate, actor)) {
                candidate.setGround(new AtmosphericChargeSource());
                return true;
            }
        }

        return false;
    }

    /**
     * Gets nearby valid map locations.
     *
     * @param map the current game map
     * @param centre the centre location
     * @return nearby valid locations
     */
    private List<Location> nearbyLocations(GameMap map, Location centre) {
        List<Location> locations = new ArrayList<>();

        for (int xOffset = -RADIUS; xOffset <= RADIUS; xOffset++) {
            for (int yOffset = -RADIUS; yOffset <= RADIUS; yOffset++) {
                int x = centre.x() + xOffset;
                int y = centre.y() + yOffset;

                if (isInsideMap(map, x, y)) {
                    locations.add(map.at(x, y));
                }
            }
        }

        return locations;
    }

    /**
     * Checks whether coordinates are inside the current map.
     *
     * @param map the current game map
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the coordinates exist on the map
     */
    private boolean isInsideMap(GameMap map, int x, int y) {
        return map.getXRange().contains(x) && map.getYRange().contains(y);
    }

    /**
     * Checks whether an AtmosphericChargeSource can be placed on a tile.
     *
     * @param location the target location
     * @param actor the actor used to test passability
     * @return true if a charge source can be placed
     */
    private boolean canPlaceChargeSource(Location location, Actor actor) {
        return !location.containsAnActor()
                && location.getGround().canActorEnter(actor)
                && !location.getGround().hasAbility(MaterialCapability.ENERGIZED)
                && !location.getGround().hasAbility(FacilityCapability.FACILITY_TERMINAL);
    }
}