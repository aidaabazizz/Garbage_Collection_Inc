package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.DamageOverTimeStatus;
import game.enums.MaterialCapability;

/**
 * A complex status effect representing an actor being supercharged with galvanic energy.
 *
 * The ShockedStatus is the "Conduit" component of the High-Voltage Galvanic System (Requirement 3).
 * It utilizes inheritance from {@link DamageOverTimeStatus} to handle host health attrition,
 * while adding unique "Emitter" logic that turns the affected Actor into a mobile power source.
 *
 * Complexity Proof (Requirement 3):
 * This class demonstrates "Actor-to-Environment Conduction." Every turn the status is active,
 * the host releases a 1-tile energy pulse that triggers cascading reactions in Grounds,
 * neighboring Actors, and Items on the floor simultaneously.
 *
 * @author Jewell Gomes
 */
public class ShockedStatus extends DamageOverTimeStatus {
    private final Display display = new Display();
    private static final int DAMAGE = 1;
    /**
     * Constructor for ShockedStatus.
     *
     * @param turns The duration of the effect. Standard galvanic exposure
     *              typically results in a 2-turn charge.
     */
    public ShockedStatus(int turns) {
        super("Shocked", turns); // normally just 2 turns of shocked
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
        if (isStatusActive()) {
            entity.enableAbility(MaterialCapability.CONDUCTIVE);
        }

        super.tickStatus(entity, location);
        if (remainingTurns <= 0) {
            entity.disableAbility(MaterialCapability.CONDUCTIVE);
        }
        String conduitName = entity+ "'s electric conduit";
        ChargeContext pulse = new GalvanicCharge(conduitName, display, DAMAGE);

        pulse.visit(location);
        /*
         * the "Human Lightning Bolt" AoE Pulse.
         * Bob's body becomes a ChargeSource. We iterate through
         * all exits to simulate energy "leaking" into the adjacent tiles.
         */
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            ChargeUtils.triggerGroundReaction(adj, pulse);


            ChargeUtils.zapTile(adj, pulse, false);
        }
    }
}
