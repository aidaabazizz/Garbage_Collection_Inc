package game.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.DistortionCapability;
import game.sanctuary.DamageInterceptor;

public class SanctuaryField extends Ground {

    private int remainingTurns;
    private final Ground previousGround;

    public SanctuaryField(int duration, Ground previousGround) {
        super('✦', "Sanctuary Field");
        this.remainingTurns = duration;
        this.previousGround = previousGround;
        this.enableAbility(DistortionCapability.SANCTUARY);
    }

    @Override
    public void tick(Location location) {
        // Clear first, then re-grant only to actors currently in range
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

    private void clearAndReapply(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.disableAbility(DamageInterceptor.PROTECTED);
            actor.enableAbility(DamageInterceptor.PROTECTED);
        }
    }

    private void clearProtectionAt(Location location) {
        if (location.containsAnActor()) {
            location.getActor().disableAbility(DamageInterceptor.PROTECTED);
        }
    }

    private void applyProtectionAt(Location location) {
        if (location.containsAnActor()) {
            location.getActor().enableAbility(DamageInterceptor.PROTECTED);
        }
    }
}

//    /**
//     * Sets PROTECTED flag immediately so hurt() works this same turn.
//     * FieldProtectionStatus clears the flag next tick unless re-granted.
//     * No instanceof — capability checks only (DIP).
//     */
//    private void applyProtectionAt(Location location) {
//        if (!location.containsAnActor()) return;
//
//        Actor actor = location.getActor();
//
//        // Immediate flag — no tick delay
//        actor.enableAbility(DamageInterceptor.PROTECTED);
//
//        // Status manages flag expiry — only add if not already present
//        if (!actor.hasStatus(FieldProtectionStatus.class)) {
//            actor.addStatus(new FieldProtectionStatus());
//        }
//    }
//}