package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.MaterialCapability;
import game.highvoltage.*;

/**
 * A temporary hazardous terrain representing a puddle charged with high-voltage electricity.
 *
 * The ElectrifiedPuddle is a multi-stage resonator in the High-Voltage Galvanic System (Requirement 3).
 * It acts as both a continuous power source for magnetic equipment and a dangerous
 * environmental hazard for all actors.
 *
 * Complexity Proof (Requirement 3):
 * 1. Timed Terrain Lifecycle: Automatically reverts to a harmless standard Puddle
 *    after its lifespan expires.
 * 2. Energy Refreshment: Implements {@link ChargeReactive} to allow external energy sources
 *    to stack its lifespan, creating a sustainable environmental hazard.
 * 3. Direct-Contact Hazard: Penalizes any occupant with immediate damage and ShockedStatus.
 * 4. Proximity Arcing (AoE): Scans all 8 neighboring tiles to apply ParalyzedStatus
 *    with a 20% probability, simulating realistic electrical conductivity.
 *
 * @author Jewell Gomes
 */
public class ElectrifiedPuddle extends Ground  implements ChargeReactive {
    /** The number of turns remaining before the hazard dissipates. */
    private int lifeSpan = INITIAL_LIFESPAN;
    private static final int INITIAL_LIFESPAN = 8;
    /** The probability (20%) of electricity arcing to an adjacent tile. */
    private static final double PARALYZED_CHANCE = 0.20;
    private final Display display = new Display();

    /**
     * Constructor for the ElectrifiedPuddle.
     * Initializes the ground with the death/hazard icon ('☠').
     * Sets the ENERGIZED capability to provide a continuous power source for magnetic tools.
     */
    public ElectrifiedPuddle() {
        super('☠', "Electrified Puddle");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    /**
     * Implements the ChargeReactive interface to handle secondary energy strikes.
     *
     * Instead of morphing, an already ElectrifiedPuddle "absorbs" incoming charges
     * to increment its internal turn counter. This allows the hazard to persist
     * indefinitely if repeatedly struck by lightning or tesla coils.
     *
     * @param location The coordinate of the puddle receiving the charge.
     * @param charge   The ChargeContext representing the incoming surge.
     */
    @Override
    public void reactToCharge(Location location, ChargeContext charge) {
        // use += to stack the lifespan
        this.lifeSpan = Math.min(this.lifeSpan + INITIAL_LIFESPAN, INITIAL_LIFESPAN * 3);

        // show the new total so the player knows it stacked
        charge.getDisplay().println(String.format("\u001B[33m %s absorbs energy from %s! (+%d turns)\u001B[0m",
                this, charge.getSourceName(), INITIAL_LIFESPAN));
    }

    /**
     * Executes the hazardous logic every turn.
     *
     * The method performs three distinct operational phases:
     * 1. Lifecycle Decay: Decrements the lifespan and reverts the tile to a
     *    standard {@link Puddle} if the energy is depleted.
     * 2. Direct Contact Check: If an actor is on the tile, they take 1 damage
     *    and receive a {@link ShockedStatus}.
     * 3. Proximity Arcing: Iterates through all 8 adjacent exits; if an actor is
     *    present, there is a 20% chance they will receive a {@link ParalyzedStatus}.
     *
     * @param location The coordinate of the ElectrifiedPuddle.
     */
    @Override
    public void tick(Location location) {
        this.lifeSpan--;

        if (this.lifeSpan <= 0) {
            location.setGround(new Puddle());
            return;
        }

        if (location.containsAnActor()) {
            Actor victim = location.getActor();
            victim.hurt(1);
            victim.addStatus(new ShockedStatus(2));
        }

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor()) {
                Actor neighbor = adj.getActor();
                if (Math.random() < PARALYZED_CHANCE) {
                    neighbor.addStatus(new ParalyzedStatus(1));
                    neighbor.enableAbility(MaterialCapability.PARALYZED);
                    neighbor.enableAbility(MaterialCapability.REFLECTIVE);
                    display.println("\u001B[33m" + " Arcs of electricity jump from the puddle and paralyze " + neighbor + "!" + "\u001B[0m");
                }
            }
        }
    }
}
