package game.weather.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.FireStackable;
import game.enums.DistortionCapability;
import game.grounds.BlueFire;
import game.weather.WeatherSnapshot;
import game.enums.FacilityCapability;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Weather effect that turns high temperature into stronger distortion hazards.
 * <p>
 * This effect connects REQ5 to the Distorted Sanctuary system from REQ4.
 * Heat strengthens existing fire hazards and causes corrupted or sanctuary
 * grounds to radiate BlueFire into nearby valid tiles.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class HeatDistortionEffect implements AnomalyWorldEffect {

    private static final int RADIUS = 2;
    private static final double HEAT_THRESHOLD = 32.0;
    private static final double BLUE_FIRE_SPREAD_CHANCE = 0.45;
    private static final int HEAT_BLUE_FIRE_LIFESPAN = 5;

    private final Random random;

    /**
     * Constructor for HeatDistortionEffect.
     */
    public HeatDistortionEffect() {
        this(new Random());
    }

    /**
     * Constructor for HeatDistortionEffect with injectable randomness.
     *
     * @param random the random number generator
     */
    public HeatDistortionEffect(Random random) {
        this.random = random;
    }

    /**
     * Checks whether this effect should apply to the current weather snapshot.
     *
     * @param snapshot the weather snapshot
     * @return true if temperature should activate heat distortion
     */
    @Override
    public boolean canApply(WeatherSnapshot snapshot) {
        return snapshot.getTemperature() >= HEAT_THRESHOLD;
    }

    /**
     * Applies heat distortion around the actor.
     *
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @param location the actor's current location
     * @param snapshot the weather snapshot
     * @return a description of the heat distortion effects
     */
    @Override
    public String applyEffect(Actor actor, GameMap map, Location location, WeatherSnapshot snapshot) {
        List<Location> nearbyLocations = nearbyLocations(map, location);

        int fireStacksAdded = strengthenExistingFire(nearbyLocations);
        int blueFiresCreated = spreadBlueFireFromDistortionGrounds(nearbyLocations, actor);

        return buildResultMessage(fireStacksAdded, blueFiresCreated);
    }

    /**
     * Strengthens existing stackable fire hazards.
     *
     * @param locations nearby locations
     * @return number of fire hazards strengthened
     */
    private int strengthenExistingFire(List<Location> locations) {
        int fireStacksAdded = 0;

        for (Location target : locations) {
            FireStackable fire = target.getGroundAs(FireStackable.class);

            if (fire != null) {
                fire.addStack();
                fireStacksAdded++;
            }
        }

        return fireStacksAdded;
    }

    /**
     * Spreads BlueFire from corrupted or sanctuary grounds.
     *
     * @param locations nearby locations
     * @param actor the actor used to test passability
     * @return number of BlueFire tiles created
     */
    private int spreadBlueFireFromDistortionGrounds(List<Location> locations, Actor actor) {
        int blueFiresCreated = 0;

        for (Location source : locations) {
            if (isHeatReactiveDistortionGround(source)) {
                blueFiresCreated += spreadBlueFireAround(source, actor);
            }
        }

        return blueFiresCreated;
    }

    /**
     * Spreads BlueFire around a distortion ground.
     *
     * @param source the source location
     * @param actor the actor used to test passability
     * @return number of BlueFire tiles created
     */
    private int spreadBlueFireAround(Location source, Actor actor) {
        int blueFiresCreated = 0;

        for (Location target : adjacentLocations(source)) {
            if (canBecomeBlueFire(target, actor) && random.nextDouble() < BLUE_FIRE_SPREAD_CHANCE) {
                Ground previousGround = target.getGround();
                target.setGround(new BlueFire(HEAT_BLUE_FIRE_LIFESPAN, previousGround));
                blueFiresCreated++;
            }
        }

        return blueFiresCreated;
    }

    /**
     * Checks whether the ground should react to heat distortion.
     *
     * @param location the location to check
     * @return true if the ground is corrupted or sanctuary-based
     */
    private boolean isHeatReactiveDistortionGround(Location location) {
        return location.getGround().hasAbility(DistortionCapability.CORRUPTED)
                || location.getGround().hasAbility(DistortionCapability.SANCTUARY);
    }

    /**
     * Checks whether a tile can become BlueFire.
     *
     * @param location the target location
     * @param actor the actor used to test passability
     * @return true if BlueFire can be placed
     */
    private boolean canBecomeBlueFire(Location location, Actor actor) {
        return !location.containsAnActor()
                && location.getGround().canActorEnter(actor)
                && !location.getGround().hasAbility(DistortionCapability.CORRUPTED)
                && !location.getGround().hasAbility(DistortionCapability.SANCTUARY)
                && !location.getGround().hasAbility(DistortionCapability.ACTIVE_HAZARD)
                && !location.getGround().hasAbility(FacilityCapability.FACILITY_TERMINAL);
    }

    /**
     * Builds the result message.
     *
     * @param fireStacksAdded number of existing fires strengthened
     * @param blueFiresCreated number of BlueFire tiles created
     * @return effect result message
     */
    private String buildResultMessage(int fireStacksAdded, int blueFiresCreated) {
        if (fireStacksAdded == 0 && blueFiresCreated == 0) {
            return "Heat distortion shimmers, but no nearby distortion hazards respond.";
        }

        return "Heat distortion intensifies the sanctuary system: "
                + fireStacksAdded + " fire hazard(s) strengthened and "
                + blueFiresCreated + " BlueFire tile(s) created.";
    }

    /**
     * Gets valid map locations within the configured radius.
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
     * Gets adjacent valid locations.
     *
     * @param centre the centre location
     * @return adjacent locations
     */
    private List<Location> adjacentLocations(Location centre) {
        List<Location> locations = new ArrayList<>();

        centre.getExits().forEach(exit -> locations.add(exit.getDestination()));

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
}