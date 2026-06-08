package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.MaterialCapability;

/**
 * A complex status effect representing an actor being supercharged with galvanic energy.
 *
 * DESIGN REFACTOR:
 * This class now implements Status directly (Composition) rather than extending
 * DamageOverTimeStatus. This avoids "Inheritance for Code Reuse" and ensures the
 * class defines its own conduit logic.
 *
 * @author Jewell Gomes
 */
public class ShockedStatus implements Status {
    private int remainingTurns;
    private final Display display = new Display();
    private static final int DAMAGE_TO_HOST = 1;
    private static final int AOE_PULSE_DAMAGE = 1;

    /**
     * Constructor for ShockedStatus.
     *
     * @param turns The duration of the effect. Standard galvanic exposure
     *              typically results in a 2-turn charge.
     */
    public ShockedStatus(int turns) {
        this.remainingTurns = turns;
    }

    /**
     * Executes the status logic during the host's tick cycle.
     *
     * This method manages a multiphase electrical discharge:
     * 1. Material State Change: Enables {@link MaterialCapability#CONDUCTIVE} on the host
     *    so they act as a reflective hazard in combat.
     * 2. Host Damage: Invokes the superclass to apply standard damage-over-time to the host.
     * 3. Lifecycle Cleanup: Disables the {@code CONDUCTIVE} capability once the turns expire.
     * 4. Environmental Arcing: Creates a "Human Lightning Bolt" effect, scanning all 8
     *    neighboring tiles and using {@link ChargeUtils} to trigger grounds, zap actors,
     *    and power items.
     *
     * @param entity   The actor currently acting as the electric conduit.
     * @param location The coordinate of the conduit.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (!isStatusActive()) {
            return;
        }

        // Mark the host as CONDUCTIVE while the charge is active
        entity.enableAbility(MaterialCapability.CONDUCTIVE);

        // ATTRITION (Replacing the old DOT inheritance)
        // We use the location to find the actor safely without instanceof
        if (location.containsAnActor()) {
            location.getActor().hurt(DAMAGE_TO_HOST);
        }

        // ENVIRONMENTAL ARCING (The AoE Pulse)
        String conduitName = entity + "'s electric conduit";
        ChargeContext pulse = new GalvanicCharge(conduitName, display, AOE_PULSE_DAMAGE);

        // mark host tile as visited so they don't zap themselves twice
        pulse.visit(location);

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            // Trigger grounds and zap neighbors (but do not apply more statuses)
            ChargeUtils.triggerGroundReaction(adj, pulse);
            ChargeUtils.zapTile(adj, pulse, false);
        }

        remainingTurns--;

        if (remainingTurns <= 0) {
            entity.disableAbility(MaterialCapability.CONDUCTIVE);
        }
    }

    /**
     * Checks if the effect is still operational based on remaining turns.
     *
     * @return True if turns remain; false if the effect has expired.
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    /**
     * @return A string representation including the remaining duration.
     */
    @Override
    public String toString() {
        return "Shocked (Conductive Conduit)";
    }
}