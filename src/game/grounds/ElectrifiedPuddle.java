package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ParalyzedStatus;
import game.highvoltage.ShockedStatus;

/**
 * A temporary hazardous terrain representing a puddle charged with high-voltage electricity.
 *
 * The ElectrifiedPuddle is a multi-stage resonator in the High-Voltage Galvanic System (REQ3).
 * It acts as both a continuous power source for magnetic equipment and a dangerous
 * environmental hazard for all actors.
 *
 * Complexity Proof (Rule 2):
 * 1. Timed Terrain Lifecycle: Automatically reverts to a harmless standard Puddle
 *    after a set number of turns.
 * 2. Energy Refreshment: Implements ChargeReactive to allow energy sources (like Lightning
 *    or Tesla Coils) to reset its lifespan, creating a sustainable hazard.
 * 3. Indiscriminate On-Tile Hazard: Applies damage and ShockedStatus to any occupant.
 * 4. Proximity Arcing (AoE): Scans all 8 neighboring tiles to apply ParalyzedStatus
 *    with a 20% probability, simulating realistic electrical arcing.
 *
 * @author Jewell Gomes
 */
public class ElectrifiedPuddle extends Ground  implements ChargeReactive {
    /** The number of turns remaining before the hazard dissipates. */
    private int lifeSpan = 8;
    /** The probability (20%) of electricity arcing to an adjacent tile. */
    private static final double PARALYZED_CHANCE = 0.20;

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
     * Instead of morphing, an already ElectrifiedPuddle "absorbs" the new charge to
     * reset its internal turn counter, extending the duration of the hazard.
     *
     * @param location   The coordinate of the puddle.
     * @param display    The terminal interface for outputting refresh messages.
     * @param sourceName The name of the energy source hitting the puddle.
     */
    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        // refresh the lifespan if struck again
        display.println("The electrified puddle is surged by " + sourceName + " and its energy is refreshed!");
        this.lifeSpan = 8;
    }

    /**
     * Executes the hazardous logic every turn.
     *
     * The method performs three distinct operational checks:
     * 1. Lifecycle Check: Reverts to a standard Puddle if lifeSpan reaches zero.
     * 2. Direct Contact Check: Punishes any actor standing on the tile with 1 damage
     *    and a ShockedStatus.
     * 3. Proximity Arcing Check: Iterates through all 8 adjacent exits and attempts
     *    to apply a ParalyzedStatus to nearby actors via a randomized probability check.
     *
     * @param location The coordinate of the ElectrifiedPuddle.
     */
    @Override
    public void tick(Location location) {
        Display display = new Display();
        lifeSpan--;

        if (lifeSpan <= 0) {
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
                    display.println("\u001B[33m" + "⚡ Arcs of electricity jump from the puddle and paralyze " + neighbor + "!" + "\u001B[0m");
                }
            }
        }
    }
}
