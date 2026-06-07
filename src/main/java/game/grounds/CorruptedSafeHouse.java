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
 * A protective structure that acts as a haven for workers while generating a
 * defensive perimeter of BlueFire.
 *
 * The Corrupted Safe House has a complex lifecycle based on worker occupancy:
 *     Entry: When a worker enters, they receive a SanctuaryStatus
 *     and the surrounding area ignites with BlueFire.
 *     Occupancy (Turns 1-5):The worker receives passive healing and damage reduction;
 *     the surrounding BlueFire is refreshed each turn.
 *     Occupancy (Turns 6+): Protection expires but healing continues. The
 *     external fire ceases to spawn automatically.
 *     Exit:<The status expires and fire production stops immediately.
 *
 *
 * This ground can be "stabilized" to stop the spread of fire, or "audited" for
 * credits at the cost of a violent energy burst and a cooldown period.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class CorruptedSafeHouse extends Ground implements DistortionSource {

    /** Flag indicating if the safe house has been stabilized. */
    private boolean isStabilised = false;

    /** Probability of a worker being healed while inside. */
    private static final double HEAL_CHANCE = 0.30;

    /** Amount of health restored during a healing event. */
    private static final int HEAL_AMOUNT = 1;

    /** The standard lifespan of spawned Blue Fire. */
    private static final int BLUE_FIRE_LIFESPAN = 3;

    /** The lifespan of Blue Fire spawned during a violent audit burst. */
    private static final int BLUE_FIRE_BURST_LIFESPAN = 6;

    /** Credits awarded to the company for auditing this structure. */
    private static final int AUDIT_CREDITS = 20;

    /** Duration of the cooldown period following an audit. */
    private static final int AUDIT_COOLDOWN = 5;

    /** Display for sanctuary-related messages. */
    private final Display display = new Display();

    /** Remaining turns of the audit-induced cooldown. */
    private int auditCooldown = 0;

    /**
     * Constructor.
     * Initializes the safe house with the '⌂' symbol and the CORRUPTED capability.
     */
    public CorruptedSafeHouse() {
        super('⌂', "Corrupted Safe House");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    /**
     * Updates the safe house state every turn.
     *
     * If not on cooldown or stabilized, the safe house checks for a worker. If a
     * worker is present, it manages their SanctuaryStatus and handles the
     * spawning of defensive  BlueFire on adjacent tiles.
     *
     * @param location The current location of the safe house.
     */
    @Override
    public void tick(Location location) {
        if (auditCooldown > 0) {
            auditCooldown--;
            if (auditCooldown == 0) {
                isStabilised = false; // cooldown over safe house works normally again
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
     * Spawns BlueFire on adjacent tiles.
     *
     * This method implements complex interaction logic: if a tile already
     * contains fire (implementing FireStackable), it refreshes that fire's
     * lifespan instead of creating a new instance. It will not overwrite
     * impassable ground, other corrupted tiles, or existing hazards.
     *
     * @param location The current location of the safe house.
     */
    private void spawnBlueFire(Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            Ground existing = dest.getGround();

            // Check if the tile is already fire using the interface
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

    /**
     * Suppresses the spatial distortion of the safe house.
     * Prevents further spawning of Blue Fire and removes the CORRUPTED capability.
     *
     * @param location The location of the safe house.
     */
    @Override
    public void stabilise(Location location) {
        isStabilised = true;
        this.disableAbility(DistortionCapability.CORRUPTED);
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