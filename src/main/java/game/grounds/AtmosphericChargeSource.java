package game.grounds;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.MaterialCapability;
import game.highvoltage.*;

import java.util.Random;

/**
 * A specialized Ground tile that acts as a map-wide lightning generator.
 *
 * The AtmosphericChargeSource represents the unpredictable environmental energy of the
 * moon facility. Every turn, it has a randomized chance to strike any coordinate on
 * the current map, acting as the primary autonomous "Emitter" for the
 * High-Voltage Galvanic System (Requirement 3).
 *
 * Complexity Proof (Requirement 3):
 * 1. Remote State Manipulation: Unlike standard grounds that only affect their own
 *    coordinate, this class selects random targets across the entire map.
 * 2. Cascading Surge Logic: A single strike initiates a chain of interactions
 *    including terrain morphing, combat status effects, and metabolic metamorphosis.
 * 3. Indiscriminate Emitter: Demonstrates the ability for the environment to
 *    interact with Actors and Items simultaneously via the Galvanic system.
 *
 * @author Jewell Gomes
 */
public class AtmosphericChargeSource extends Ground implements ChargeSource {
    /** The random number generator for strike location and timing. */
    private final Random rand = new Random();
    /** The probability (10%) of a lightning strike occurring on any given turn. */
    private static final double LIGHTNING_CHANCE = 0.10;
    /**
     * The amount of damage dealt to an Actor directly hit by a bolt.
     */
    private static final int DAMAGE = 3;
    private final Display display = new Display();

    /**
     * Constructor for the AtmosphericChargeSource.
     * Initializes the ground with the storm icon ('⛈').
     * Sets the ENERGIZED capability so that actors standing on this tile can power equipment.
     */
    public AtmosphericChargeSource() {
        super('⛈', "Atmospheric Charge Source");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    /**
     * Executes the autonomous turn logic.
     *
     * Every turn, there is a 10% chance that a lightning bolt is generated.
     * If triggered, the method:
     * 1. Calculates a random coordinate within the map's valid X and Y ranges.
     * 2. Logs the strike location to the console with high-visibility color.
     * 3. Releases a {@link GalvanicCharge} at the target location.
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
            ChargeContext bolt = new GalvanicCharge("a massive Lightning Bolt", display, DAMAGE);
            String yellow = "\u001B[33m";
            String reset = "\u001B[0m";
            display.println(yellow + "⛈ A bolt strikes the facility at (" + strikeX + ", " + strikeY + ")!" + reset);
            this.releaseCharge(strikePoint, bolt);
        }
    }

    /**
     * Implements the ChargeSource interface to release energy into a specific location.
     *
     * This method utilizes the Dependency Inversion Principle (DIP) and helper utilities
     * to propagate energy through the target tile's components.
     *
     * Propagation Sequence:
     * 1. Ground Reaction: Checks if the tile's Ground is reactive (e.g., morphing Puddles).
     * 2. Tile Zap: Processes damage and statuses for Actors, triggers metamorphosis
     *    in reactive creatures, and powers reactive items in inventories or on the floor.
     *
     * @param location The location being struck by the lightning bolt.
     * @param charge   The ChargeContext containing source info and damage values.
     */
    @Override
    public void releaseCharge(Location location, ChargeContext charge) {
        ChargeUtils.triggerGroundReaction(location, charge);

        ChargeUtils.zapTile(location, charge, true);
    }
}
