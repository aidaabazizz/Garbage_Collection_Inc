package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.enums.Ability;
import game.capabilities.Consumable;
import game.capabilities.PoisonStatus;
import game.highvoltage.ChargeContext;
import game.highvoltage.ChargeReactive;

/**
 * A body of liquid on the ground that can be consumed by actors.
 * The effects of consuming the liquid depend on whether the consumer
 * possesses sterilization capabilities, leading to either healing or poisoning.
 *
 * The Puddle implements the {@link ChargeReactive} interface, allowing it to function
 * as a "Resonator" within the High-Voltage Galvanic System (Requirement 3).
 *
 * Complexity Proof (Requirement 3):
 * This class demonstrates "Structural Terrain Morphing." When exposed to a galvanic
 * charge, the Puddle physically removes itself from the GameMap and replaces itself
 * with a high-energy hazardous Ground type (ElectrifiedPuddle), representing a
 * fundamental change to the map's topology.
 *
 * @author Jewell Gomes
 */
public class Puddle extends Ground implements Consumable, ChargeReactive {

    /** The amount of health restored when drinking purified water. */
    private static final int HEAL_POINTS = 1;

    /** The duration of the poison status effect when drinking toxic water. */
    private static final int POISON_DURATION = 3;

    /**
     * Constructs a new Puddle instance.
     * Initializes the puddle with a display character of '~' and the display name "Puddle".
     * The puddle remains permanently on the map unless replaced by other ground types.
     */
    public Puddle() {
        super('~', "Puddle");
    }

    /**
     * Implements the ChargeReactive interface to handle high-voltage transformation.
     *
     * When hit by a ChargeSource (e.g., Lightning or a Battery Surge), the puddle
     * conducts the energy and undergoes a permanent structural map change.
     *
     * Transformation Steps:
     * 1. Logs the event to the display using high-visibility cyan text.
     * 2. Programmatically replaces this Ground instance at the given location
     *    with a new {@link ElectrifiedPuddle}.
     *
     * @param location The coordinate of the puddle being hit.
     * @param charge   The ChargeContext representing the incoming surge.
     */
    @Override
    public void reactToCharge(Location location, ChargeContext charge) {
        // physically replaces this ground instance with the ElectrifiedPuddle hazard.
        charge.getDisplay().println("\u001B[36m" + "The puddle is hit by " + charge.getSourceName() +
                " and becomes electrified!" + "\u001B[0m");
        location.setGround(new ElectrifiedPuddle());
    }

    /**
     * Generates a list of actions available to an actor standing on the puddle.
     * The description of the water changes based on the actor's abilities.
     * @param actor The actor performing the check.
     * @param location The location of the puddle.
     * @param direction The direction of the interaction.
     * @return A list of valid consume actions for the actor.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            if (actor.hasAbility(Ability.STERILIZER)) {
                actions.add(new ConsumeAction(this, "Puddle water (Purified)"));
            } else {
                actions.add(new ConsumeAction(this, "Puddle water (Toxic)"));
            }
        }
        return actions;
    }

    /**
     * Indicates if the puddle is depleted after consumption.
     * @return Always false as puddles are permanent environmental features.
     */
    @Override
    public boolean isFinished() {
        return false;
    }

    /**
     * Processes the consumption logic when an actor drinks from the puddle.
     * Heals the actor if they have sterilizer capability; otherwise,
     * applies a poison status.
     * @param actor The actor drinking the water.
     * @return A string describing the outcome of the consumption.
     */
    @Override
    public String consumedBy(Actor actor) {
        if (actor.hasAbility(Ability.STERILIZER)) {
            actor.heal(HEAL_POINTS);
            return String.format("%s drinks purified water from the puddle and heals %d HP.", actor, HEAL_POINTS);
        } else {
            actor.addStatus(new PoisonStatus(POISON_DURATION));
            return String.format("%s drinks toxic liquid from the puddle and is poisoned for %d turns!", actor, POISON_DURATION);
        }
    }
}
