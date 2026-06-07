package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.DistortionCapability;
import game.sanctuary.DamageInterceptor;

/**
 * A temporary protective zone that grants damage immunity to nearby actors.
 *
 * The Sanctuary Field acts as a dynamic aura. Every turn, it grants the
 * PROTECTED ability to any actor standing on its tile
 * or any of its adjacent tiles (radius 1). This ground type is typically
 * manifested by a HeavenToken
 *
 * Once its duration expires, the field removes the protection from all
 * affected tiles and reverts the location back to its {@code previousGround} state.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class SanctuaryField extends Ground {
    /** The number of turns remaining before the field collapses. */
    private int remainingTurns;

    /** The original ground type to be restored when the field expires. */
    private final Ground previousGround;

    /**
     * Constructor.
     * Initializes the field with the '✦' symbol and the SANCTUARY capability.
     *
     * @param duration       the number of turns the field will persist.
     * @param previousGround the ground that was at this location before the field appeared.
     */
    public SanctuaryField(int duration, Ground previousGround) {
        super('✦', "Sanctuary Field");
        this.remainingTurns = duration;
        this.previousGround = previousGround;
        this.enableAbility(DistortionCapability.SANCTUARY);
    }

    /**
     * Updates the field's state every turn.
     *
     * This method implements the aura logic:
     *     Refreshes protection for actors on the current tile and all adjacent exit tiles.
     *     Decrements the remaining lifespan.
     *     If the lifespan reaches zero, it strips protection from all nearby actors
     *     and reverts the ground to the stored {@code previousGround}.

     *
     * @param location the location of the Sanctuary Field.
     */
    @Override
    public void tick(Location location) {
        // Clear first then re grant only to actors currently in range
        clearAndReapply(location);
        for (Exit exit : location.getExits()) {
            clearAndReapply(exit.getDestination());
        }

        remainingTurns--;
        if (remainingTurns <= 0) {
            // Clear protection before restoring ground
            clearProtectionAt(location);
            for (Exit exit : location.getExits()) {
                clearProtectionAt(exit.getDestination());
            }
            location.setGround(previousGround);

        }
    }

    /**
     * Refreshes the protection status for an actor at a specific location.
     *
     * By disabling and then enabling the ability, we ensure that actors
     * moving out of the field's range lose protection naturally, while
     * those staying inside have it consistently maintained.
     *
     * @param location the location to check for an actor.
     */
    private void clearAndReapply(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.disableAbility(DamageInterceptor.PROTECTED);
            actor.enableAbility(DamageInterceptor.PROTECTED);
        }
    }

    /**
     * Explicitly removes the protection ability from an actor at the given location.
     * Used when the field's lifespan expires.
     *
     * @param location the location to check for an actor.
     */
    private void clearProtectionAt(Location location) {
        if (location.containsAnActor()) {
            location.getActor().disableAbility(DamageInterceptor.PROTECTED);
        }
    }

}
