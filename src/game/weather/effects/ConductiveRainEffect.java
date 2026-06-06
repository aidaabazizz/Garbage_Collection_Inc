package game.weather.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.DistortionCapability;
import game.enums.MaterialCapability;
import game.grounds.Puddle;
import game.highvoltage.ChargeContext;
import game.highvoltage.ChargeReactive;
import game.highvoltage.GalvanicCharge;
import game.sanctuary.Extinguishable;
import game.weather.WeatherSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Weather effect that turns high humidity or rain into conductive moisture.
 * <p>
 * This effect connects the API system in REQ5 to both the High-Voltage system
 * from REQ3 and the Distorted Sanctuary system from REQ4. It creates puddles,
 * triggers charge-reactive terrain, and may suppress extinguishable hazards
 * such as BlueFire without depending on concrete hazard classes.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class ConductiveRainEffect implements AnomalyWorldEffect {

    private static final int RADIUS = 2;
    private static final int HUMIDITY_THRESHOLD = 75;
    private static final double PUDDLE_SPAWN_CHANCE = 0.35;
    private static final double EXTINGUISH_CHANCE = 0.40;
    private static final int MOISTURE_CHARGE_DAMAGE = 0;

    private final Random random;

    /**
     * Constructor for ConductiveRainEffect.
     */
    public ConductiveRainEffect() {
        this(new Random());
    }

    /**
     * Constructor for ConductiveRainEffect with injectable randomness.
     *
     * @param random the random number generator
     */
    public ConductiveRainEffect(Random random) {
        this.random = random;
    }

    /**
     * Checks whether this effect should apply to the current weather snapshot.
     *
     * @param snapshot the weather snapshot
     * @return true if humidity or rain should activate conductive moisture
     */
    @Override
    public boolean canApply(WeatherSnapshot snapshot) {
        return snapshot.getHumidity() >= HUMIDITY_THRESHOLD
                || snapshot.conditionContains("rain");
    }

    /**
     * Applies conductive moisture around the actor.
     *
     * @param actor the actor triggering the weather sync
     * @param map the current game map
     * @param location the actor's current location
     * @param snapshot the weather snapshot
     * @return a description of the terrain changes caused by moisture
     */
    @Override
    public String applyEffect(Actor actor, GameMap map, Location location, WeatherSnapshot snapshot) {
        List<Location> nearbyLocations = nearbyLocations(map, location);
        ChargeContext charge = createMoistureCharge();

        int hazardsSuppressed = suppressExtinguishableHazards(nearbyLocations);
        int reactionsTriggered = triggerReactiveGrounds(nearbyLocations, charge);
        int puddlesCreated = formPuddles(nearbyLocations, actor);

        return buildResultMessage(puddlesCreated, reactionsTriggered, hazardsSuppressed);
    }

    /**
     * Creates the charge context used by conductive moisture.
     *
     * @return the charge context
     */
    private ChargeContext createMoistureCharge() {
        return new GalvanicCharge(
                "conductive moisture",
                new Display(),
                MOISTURE_CHARGE_DAMAGE
        );
    }

    /**
     * Suppresses extinguishable hazards such as BlueFire.
     *
     * @param locations nearby locations
     * @return number of hazards suppressed
     */
    private int suppressExtinguishableHazards(List<Location> locations) {
        int hazardsSuppressed = 0;

        for (Location target : locations) {
            Extinguishable extinguishable = target.getGroundAs(Extinguishable.class);

            if (extinguishable != null && random.nextDouble() < EXTINGUISH_CHANCE) {
                extinguishable.extinguish(target);
                hazardsSuppressed++;
            }
        }

        return hazardsSuppressed;
    }

    /**
     * Triggers charge reactions on nearby reactive grounds.
     *
     * @param locations nearby locations
     * @param charge the charge context
     * @return number of triggered reactions
     */
    private int triggerReactiveGrounds(List<Location> locations, ChargeContext charge) {
        int reactionsTriggered = 0;

        for (Location target : locations) {
            ChargeReactive reactiveGround = target.getGroundAs(ChargeReactive.class);

            if (reactiveGround != null && charge.visit(target)) {
                reactiveGround.reactToCharge(target, charge);
                reactionsTriggered++;
            }
        }

        return reactionsTriggered;
    }

    /**
     * Forms puddles on valid nearby tiles.
     *
     * @param locations nearby locations
     * @param actor the actor used to check passability
     * @return number of puddles created
     */
    private int formPuddles(List<Location> locations, Actor actor) {
        int puddlesCreated = 0;

        for (Location target : locations) {
            if (canFormPuddle(target, actor) && random.nextDouble() < PUDDLE_SPAWN_CHANCE) {
                target.setGround(new Puddle());
                puddlesCreated++;
            }
        }

        return puddlesCreated;
    }

    /**
     * Builds the result message for the effect.
     *
     * @param puddlesCreated number of puddles created
     * @param reactionsTriggered number of reactive grounds surged
     * @param hazardsSuppressed number of distortion hazards suppressed
     * @return the result message
     */
    private String buildResultMessage(int puddlesCreated, int reactionsTriggered, int hazardsSuppressed) {
        if (puddlesCreated == 0 && reactionsTriggered == 0 && hazardsSuppressed == 0) {
            return "Conductive moisture gathers, but no nearby terrain is affected.";
        }

        return "Conductive moisture spreads through the facility: "
                + puddlesCreated + " puddle(s) formed, "
                + reactionsTriggered + " reactive ground(s) surged, and "
                + hazardsSuppressed + " distortion hazard(s) suppressed.";
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
     * Checks whether a puddle can safely form on this tile.
     *
     * @param location the target location
     * @param actor the actor used to test passability
     * @return true if a puddle may form
     */
    private boolean canFormPuddle(Location location, Actor actor) {
        return !location.containsAnActor()
                && location.getGround().canActorEnter(actor)
                && !location.getGround().hasAbility(MaterialCapability.ENERGIZED)
                && !location.getGround().hasAbility(DistortionCapability.CORRUPTED)
                && !location.getGround().hasAbility(DistortionCapability.SANCTUARY)
                && !location.getGround().hasAbility(DistortionCapability.ACTIVE_HAZARD);
    }
}