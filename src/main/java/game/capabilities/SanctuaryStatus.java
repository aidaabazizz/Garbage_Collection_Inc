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
 * A sanctuary based status effect granted by CorruptedSafeHouse
 * when a worker remains on the corrupted tile.
 * This status provides temporary protection and passive healing while the
 * actor remains within the sanctuary. Protection decays over time, after which
 * only healing persists.
 *
 * @author Chathya Attanayake
 * @version 1.0
 *
 */
public class SanctuaryStatus implements Status {

    /**
     * The initial number of turns the protection ability remains active.
     */
    private static final int PROTECTION_DURATION = 6;

    /**
     * The probability (0.0 to 1.0) of the actor being healed each turn.
     */
    private static final double HEAL_CHANCE = 0.30;

    /**
     * The amount of health points restored during a successful healing event.
     */
    private static final int HEAL_AMOUNT = 1;

    /**
     * Remaining turns of protection.
     */
    private int protectionTurns;

    /**
     * Tracks whether the status is still active.
     */
    private boolean isActive;

    /**
     * Display used to output healing messages to the console.
     */
    private final Display display = new Display();

    /**
     * Creates the status with the default protection duration and sets it to active.
     */
    public SanctuaryStatus() {
        this.protectionTurns = PROTECTION_DURATION;
        this.isActive = true;
    }

    /**
     * Updates the status of the entity every turn.
     *
     * This method performs the following logic:
     *
     *  Checks if the entity is an Actor and has the is a Worker
     *  Checks if the actor is still standing on a CORRUPTED ground.
     *  If either check fails, the status is expired.
     *  Applies a random chance to heal the actor.
     *  Decrements the protection timer and updates the actor's PROTECTED ability.
     *
     *
     * @param entity   The GameEntity (Actor) affected by this status.
     * @param location The current location of the entity.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        entity.asCapability(Actor.class).ifPresent(actor -> {

            // Only applies to workers
            if (!actor.hasAbility(Ability.WORKER)) {
                expire(actor);
                return;
            }

            // Actor has left the safe house, expire immediately
            if (!location.getGround().hasAbility(DistortionCapability.CORRUPTED)) {
                expire(actor);
                return;
            }

            // Actor is still on the tile, apply healing
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
     * Expires the status and removes any active protection effects.
     *
     * @param actor the actor affected by expiration
     */
    private void expire(Actor actor) {
        isActive = false;
        actor.disableAbility(DamageInterceptor.PROTECTED);
    }

    /**
     * Checks whether damage protection is still active.
     *
     * @return true if protection duration has not expired
     */
    public boolean isProtectionActive() {
        return protectionTurns > 0;
    }

    /**
     * Checks whether this status is still active.
     *
     * @return true if the status has not been expired
     */
    @Override
    public boolean isStatusActive() {
        return isActive;
    }

    /**
     * Returns a readable description of this status.
     *
     * @return status label showing remaining protection or healing state
     */
    @Override
    public String toString() {
        return protectionTurns > 0
                ? "Sanctuary Protection (" + protectionTurns + ")"
                : "Residual Healing";
    }
}