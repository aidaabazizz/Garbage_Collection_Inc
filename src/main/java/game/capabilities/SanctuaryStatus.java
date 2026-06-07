package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.sanctuary.DamageInterceptor;

/**
 * Granted by CorruptedSafeHouse when a worker stands on it.
 *
 * Lifecycle:
 * - Turns 1-5 (protectionTurns > 0): damage halved + 30% heal each turn
 * - Turns 6+  (protectionTurns == 0): healing only, no damage reduction
 * - Actor leaves tile: status expires immediately, protection flag removed
 * - Actor re-enters: CorruptedSafeHouse adds a fresh SanctuaryStatus (reset)
 */
public class SanctuaryStatus implements Status {

    private static final int PROTECTION_DURATION = 6;
    private static final double HEAL_CHANCE = 0.30;
    private static final int HEAL_AMOUNT = 1;

    private int protectionTurns;
    private boolean isActive;
    private final Display display = new Display();

    public SanctuaryStatus() {
        this.protectionTurns = PROTECTION_DURATION;
        this.isActive = true;
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        entity.asCapability(Actor.class).ifPresent(actor -> {

            // Only applies to workers
            if (!actor.hasAbility(Ability.WORKER)) {
                expire(actor);
                return;
            }

            // Actor has left the safe house — expire immediately
            if (!location.getGround().hasAbility(DistortionCapability.CORRUPTED)) {
                expire(actor);
                return;
            }

            // Actor is still on the tile — apply healing
            if (Math.random() <= HEAL_CHANCE) {
                actor.heal(HEAL_AMOUNT);
                display.println(">>> " + actor + " is healed by the sanctuary energy.");
            }

            if (protectionTurns > 0) {
                protectionTurns--;
            }

            if (protectionTurns > 0) {
                actor.enableAbility(DamageInterceptor.PROTECTED);
            } else {
                // Protection expired but status stays active for healing
                actor.disableAbility(DamageInterceptor.PROTECTED);
            }
        });
    }

    /**
     * Cleanly expires this status and removes the protection flag from the actor.
     */
    private void expire(Actor actor) {
        isActive = false;
        actor.disableAbility(DamageInterceptor.PROTECTED);
    }

    /** Used by CorruptedSafeHouse to decide whether to spawn BlueFire this turn. */
    public boolean isProtectionActive() {
        return protectionTurns > 0;
    }

    @Override
    public boolean isStatusActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return protectionTurns > 0
                ? "Sanctuary Protection (" + protectionTurns + ")"
                : "Residual Healing";
    }
}