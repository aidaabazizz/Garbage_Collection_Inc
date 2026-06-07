package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.*;
import game.capabilities.FireStackable;
import game.capabilities.SanctuaryStatus;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.managers.QuotaManager;
import game.sanctuary.DamageInterceptor;
import game.sanctuary.DistortionSource;

/**
 * A safe house that protects workers but creates a ring of BlueFire around it.
 *
 * Lifecycle (per spec):
 * - Worker enters    → fresh SanctuaryStatus added; BlueFire spawns for 5 turns
 * - Worker stays 1-5 → BlueFire re-spawns each turn; damage halved; 30% heal
 * - Worker stays 6+  → BlueFire stops (isProtectionActive() false); healing continues
 * - Worker leaves    → SanctuaryStatus expires in tickStatus(); BlueFire stops next tick
 * - Worker re-enters → old status is inactive, new SanctuaryStatus added (full reset)
 */
public class CorruptedSafeHouse extends Ground implements DistortionSource {

    private boolean isStabilised = false;

    private static final double HEAL_CHANCE = 0.30;
    private static final int HEAL_AMOUNT = 1;
    private static final int BLUE_FIRE_LIFESPAN = 3;
    private static final int BLUE_FIRE_BURST_LIFESPAN = 6;
    private static final int AUDIT_CREDITS = 20;
    private static final int AUDIT_COOLDOWN = 5;


    private final Display display = new Display();
    /** Cooldown turns after being audited. */
    private int auditCooldown = 0;


    public CorruptedSafeHouse() {
        super('⌂', "Corrupted Safe House");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    @Override
    public void tick(Location location) {
        if (auditCooldown > 0) {
            auditCooldown--;
            if (auditCooldown == 0) {
                isStabilised = false; // cooldown over — safe house works normally again
            }
            return;
        }

        if (!location.containsAnActor() || !location.getActor().hasAbility(Ability.WORKER)) {
            return;
        }

        Actor actor = location.getActor();

        // Only grant sanctuary if not stabilised
        if (!isStabilised) {
            if (!actor.hasStatus(SanctuaryStatus.class)) {
                actor.addStatus(new SanctuaryStatus());
                actor.enableAbility(DamageInterceptor.PROTECTED);
                if (Math.random() <= HEAL_CHANCE) {
                    actor.heal(HEAL_AMOUNT);
                    display.println(">>> " + actor + " is healed by the sanctuary energy.");
                }
            }

            SanctuaryStatus status = actor.statusesOf(SanctuaryStatus.class).get(0);
            if (status.isProtectionActive()) {
                spawnBlueFire(location);
            }


        }
        // If stabilised: no new status, no BlueFire, but existing status continues ticking naturally
    }
    /**
     * Spawns BlueFire on adjacent exits.
     * Guards: won't overwrite DistortionSources, impassable grounds, or existing hazards.
     */
    private void spawnBlueFire(Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            Ground existing = dest.getGround();

            // Rule 2: Complex Interaction (Using an interface to modify existing ground)
            // Check if the tile is ALREADY fire using the interface
            FireStackable fire = dest.getGroundAs(FireStackable.class);

            // Skip existing hazards (already burning)


            if (existing.hasAbility(DistortionCapability.ACTIVE_HAZARD)) {
                continue;
            }
            if (fire != null) {
                // If fire is already there, make it stronger!
                fire.addStack();
            } else {
                // If no fire, spawn new fire (if the tile is flammable)
                if (existing.canActorEnter(null) && !existing.hasAbility(DistortionCapability.CORRUPTED)) {
                    dest.setGround(new BlueFire(BLUE_FIRE_LIFESPAN, existing));
                }
            }
        }
    }

    @Override
    public String releaseDistortion(Actor actor, GameMap map, Location location) {
        return actor + " enters the sanctuary — but the air around it ignites!";
    }

    @Override
    public String stabilise(Location location) {
        isStabilised = true;
        this.disableAbility(DistortionCapability.CORRUPTED);
        return "Blue Fire spread has been suppressed!";
    }


    /**
     * Distortion Audit Protocol: The safe house vents its sanctuary energy in a
     * final burst — healing any actor inside to full, spawning a ring of BlueFire
     * with double lifespan, then entering a 5-turn cooldown (no new status granted,
     * no BlueFire spawned).
     *
     * @param quotaManager the shared quota system
     * @param location     this tile's location
     * @return audit result description
     */
    @Override
    public String audit(QuotaManager quotaManager, Location location) {
        // 1. Contribute credits to REQ1
        String creditMsg = quotaManager.addCompanyCredits(AUDIT_CREDITS);

        // 2. Final unstable effect — burst heal + double-lifespan BlueFire ring
        StringBuilder effectMsg = new StringBuilder();
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.heal(actor.getMaximumStatistic(ActorStatistics.HEALTH)); // Full heal burst
            effectMsg.append(actor).append(" is bathed in a sanctuary energy burst — fully healed!\n");
        }

        // Spawn double-lifespan BlueFire on all exits (the "violent reaction")
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            Ground existing = dest.getGround();
            if (existing.canActorEnter(null)
                    && !existing.hasAbility(DistortionCapability.CORRUPTED)
                    && !existing.hasAbility(DistortionCapability.ACTIVE_HAZARD)) {
                dest.setGround(new BlueFire(BLUE_FIRE_BURST_LIFESPAN, existing)); // Double lifespan
            }
        }
        effectMsg.append("The safe house vents in a violent burst — intensified BlueFire erupts around it!");

        // 3. Enter cooldown
        auditCooldown = AUDIT_COOLDOWN;
        isStabilised = true; // Treat as stabilised during cooldown

        return creditMsg + "\n" + effectMsg + "\nSafe house entering cooldown for 5 turns.";
    }
}