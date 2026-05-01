package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.capabilities.Ability;
import game.capabilities.Consumable;
import game.capabilities.PoisonStatus;

/**
 * A body of liquid on the ground that can be consumed by actors.
 * The effects of consuming the liquid depend on whether the consumer
 * possesses sterilization capabilities. It can either heal the actor
 * or inflict a poison status.
 *
 * @author Jewell Gomes
 */
public class Puddle extends Ground implements Consumable {

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
