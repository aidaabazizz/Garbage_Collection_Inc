package game.weather.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.DistortionCapability;
import game.enums.FacilityCapability;
import game.enums.MaterialCapability;
import game.grounds.AtmosphericChargeSource;
import game.grounds.RageGround;
import game.highvoltage.ChargeContext;
import game.highvoltage.GalvanicCharge;
import game.weather.WeatherSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Weather effect that turns storm and wind API data into stronger atmospheric
 * charge and distortion in the game world.
 * <p>
 * This effect connects REQ5 to both REQ3 and REQ4. Storm weather triggers
 * atmospheric charge through the REQ3 high-voltage system and may also spread
 * RageGround from the REQ4 distorted sanctuary system.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class StormSurgeEffect implements AnomalyWorldEffect {

    private static final int RADIUS = 2;
    private static final double WIND_THRESHOLD = 8.0;
    private static final double EXTRA_SOURCE_CHANCE = 0.50;
    private static final double RAGE_SPREAD_CHANCE = 0.50;
    private static final int STORM_CHARGE_DAMAGE = 3;

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
     * The storm releases an immediate atmospheric charge, may place an additional
     * charge source nearby, and may spread RageGround to represent distortion
     * instability caused by the storm.
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
        ChargeContext charge = createStormCharge();
        releaseAtmosphericCharge(location, charge);

        boolean sourcePlaced = false;
        boolean rageSpread = false;

        if (random.nextDouble() < EXTRA_SOURCE_CHANCE) {
            sourcePlaced = placeExtraSource(map, location, actor);
        }

        if (random.nextDouble() < RAGE_SPREAD_CHANCE) {
            rageSpread = spreadRageGround(map, location, actor);
        }

        return buildResultMessage(actor, sourcePlaced, rageSpread);
    }

    /**
     * Creates the charge context used by the storm surge.
     *
     * @return the storm charge context
     */
    private ChargeContext createStormCharge() {
        return new GalvanicCharge(
                "a weather-amplified storm surge",
                new Display(),
                STORM_CHARGE_DAMAGE
        );
    }

    /**
     * Releases atmospheric charge at the actor's location.
     *
     * @param location the charge release location
     * @param charge the storm charge context
     */
    private void releaseAtmosphericCharge(Location location, ChargeContext charge) {
        AtmosphericChargeSource surgeSource = new AtmosphericChargeSource();
        surgeSource.releaseCharge(location, charge);
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
     * Attempts to spread RageGround on a nearby valid tile.
     *
     * @param map the current game map
     * @param centre the centre location
     * @param actor the actor used to test passability
     * @return true if RageGround was spread
     */
    private boolean spreadRageGround(GameMap map, Location centre, Actor actor) {
        List<Location> candidates = nearbyLocations(map, centre);
        Collections.shuffle(candidates, random);

        for (Location candidate : candidates) {
            if (canPlaceRageGround(candidate, actor)) {
                candidate.setGround(new RageGround());
                return true;
            }
        }

        return false;
    }

    /**
     * Builds the storm surge result message.
     *
     * @param actor the actor triggering the effect
     * @param sourcePlaced whether an atmospheric charge source was placed
     * @param rageSpread whether RageGround was spread
     * @return the result message
     */
    private String buildResultMessage(Actor actor, boolean sourcePlaced, boolean rageSpread) {
        StringBuilder result = new StringBuilder();

        result.append("Storm surge intensifies the facility: atmospheric charge erupts around ")
                .append(actor)
                .append(".");

        if (sourcePlaced) {
            result.append(" A new atmospheric charge source forms nearby.");
        }

        if (rageSpread) {
            result.append(" RageGround spreads as the distortion field destabilises.");
        }

        return result.toString();
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
                && !location.getGround().hasAbility(DistortionCapability.CORRUPTED)
                && !location.getGround().hasAbility(DistortionCapability.SANCTUARY)
                && !location.getGround().hasAbility(DistortionCapability.ACTIVE_HAZARD)
                && !location.getGround().hasAbility(FacilityCapability.FACILITY_TERMINAL);
    }

    /**
     * Checks whether RageGround can be placed on a tile.
     *
     * @param location the target location
     * @param actor the actor used to test passability
     * @return true if RageGround can be placed
     */
    private boolean canPlaceRageGround(Location location, Actor actor) {
        return !location.containsAnActor()
                && location.getGround().canActorEnter(actor)
                && !location.getGround().hasAbility(MaterialCapability.ENERGIZED)
                && !location.getGround().hasAbility(DistortionCapability.CORRUPTED)
                && !location.getGround().hasAbility(DistortionCapability.SANCTUARY)
                && !location.getGround().hasAbility(DistortionCapability.ACTIVE_HAZARD)
                && !location.getGround().hasAbility(FacilityCapability.FACILITY_TERMINAL);
    }
}