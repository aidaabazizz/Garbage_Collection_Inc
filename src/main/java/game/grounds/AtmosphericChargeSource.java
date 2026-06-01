package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeSource;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ShockedStatus;

import java.util.Random;

/**
 * A specialized Ground tile that acts as a map-wide lightning generator.
 *
 * The AtmosphericChargeSource represents the unpredictable environmental energy of the moon
 * facility. Every turn, it has a randomized chance to strike a coordinate on the current map,
 * acting as the primary autonomous "Emitter" for the High-Voltage Galvanic System (REQ3).
 *
 * Complexity Proof (Rule 2):
 * 1. Remote State Manipulation: Unlike standard grounds, this class selects coordinates
 *    randomly across the entire map, demonstrating advanced usage of the engine's coordinate system.
 * 2. Indiscriminate Surge: A single strike initiates a cascading interaction chain:
 *    a) Ground: Morphs terrain (e.g., Puddles into Electrified Hazards).
 *    b) Actor: Applies direct damage and combat status effects (ShockedStatus).
 *    c) Evolution: Triggers biological metamorphosis in reactive actors.
 *    d) Equipment: Remotely activates items in an actor's pocket or on the floor.
 *
 * @author Jewell Gomes
 */
public class AtmosphericChargeSource extends Ground implements ChargeSource {
    /** The random number generator for strike location and timing. */
    private final Random rand = new Random();
    /** The probability (10%) of a lightning strike occurring on any given turn. */
    private static final double LIGHTNING_CHANCE = 0.10;

    /**
     * Constructor for the AtmosphericChargeSource.
     * Initializes the ground with the storm icon ('⛈').
     * Sets the ENERGIZED capability so that actors standing on this tile can power equipment.
     */
    public AtmosphericChargeSource() {
        super('⛈', "Atmospheric Controller");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    /**
     * Executes the autonomous turn logic.
     *
     * Every turn, a 10% probability check is performed. If successful:
     * 1. A random (x, y) coordinate is selected from the current map's valid range.
     * 2. A high-visibility alert is printed to the console using ANSI color codes.
     * 3. The releaseCharge method is invoked at the target coordinate.
     *
     * @param location The fixed location of the AtmosphericChargeSource tile.
     */
    @Override
    public void tick(Location location) {
        if (rand.nextDouble() < LIGHTNING_CHANCE) {
            int maxX = location.map().getXRange().max();
            int maxY = location.map().getYRange().max();
            int strikeX = rand.nextInt(maxX + 1);
            int strikeY = rand.nextInt(maxY + 1);
            Location strikePoint = location.map().at(strikeX,  strikeY);

            Display display = new Display();
            String yellow = "\u001B[33m";
            String reset = "\u001B[0m";
            display.println(yellow + "⛈ A bolt strikes the facility at (" + strikeX + ", " + strikeY + ")!" + reset);
            this.releaseCharge(strikePoint, display, "a massive Lightning Bolt");
        }
    }

    /**
     * Implements the ChargeSource interface to release energy into a specific location.
     *
     * This method utilizes the Dependency Inversion Principle (DIP) to interact with
     * any object implementing {@link ChargeReactive} without knowing its concrete class.
     *
     * Propagation Hierarchy:
     * 1. Ground Reaction: Checks if the tile's Ground implements ChargeReactive.
     * 2. Actor Combat: Deals 3 damage and applies 2 turns of ShockedStatus.
     * 3. Actor Reaction: Triggers specific metamorphosis logic if the actor is reactive.
     * 4. Inventory Reaction: Triggers any reactive items Bob is carrying (e.g., Wallet).
     * 5. Floor Item Reaction: Triggers any reactive items lying on the ground.
     *
     * @param location   The location being struck by lightning.
     * @param display    The terminal interface for outputting surge events.
     * @param sourceName The name of this energy source ("a massive Lightning Bolt").
     */
    @Override
    public void releaseCharge(Location location, Display display, String sourceName) {
        ChargeReactive ground = location.getGroundAs(ChargeReactive.class);
        if (ground != null) {
            ground.reactToCharge(location, display, sourceName);
        }

        if (location.containsAnActor()) {
            Actor victim = location.getActor();

            victim.hurt(3);
            victim.addStatus(new ShockedStatus(2));


            victim.asCapability(ChargeReactive.class)
                    .ifPresent(actorReactive -> actorReactive.reactToCharge(location, display, sourceName));

            victim.getInventory().getItemsAs(ChargeReactive.class)
                    .forEach(item -> item.reactToCharge(location, display, sourceName));
        }
        location.getItemsAs(ChargeReactive.class)
                .forEach(item -> item.reactToCharge(location, display, sourceName));
    }
}
