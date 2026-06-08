package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 **
 * A special combat action that performs a standard attack with an additional lifesteal effect.
 *
 * When executed, this action first delegates to AttackAction to perform normal damage
 * calculation. After a successful strike, the attacker heals for a fixed amount of health,
 * representing a "rage strike" lifesteal mechanic.
 * This action is typically used when the {@code KillerInstinctStatus} effect is active.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class RageStrikeAction extends AttackAction{
    /**
     * The target actor being attacked.
     */
    private final Actor target;

    /** Amount of HP restored to the attacker on a successful rage strike. */
    private static final int HEAL_AMOUNT = 2;

    /**
     * Constructs a new RageStrikeAction.
     *
     * @param target    the actor being attacked
     * @param direction the direction of the attack (used by the engine for display/logging)
     * @param weapon    the weapon used to perform the attack
     */
    public RageStrikeAction(Actor target, String direction, Weapon weapon) {
        super(target, direction, weapon);
        this.target = target;
    }

    /**
     * Executes the rage strike action.
     *
     * This method performs a standard attack using AttackAction
     * then applies a lifesteal effect by healing the attacking actor for a fixed amount.
     *
     *
     * @param actor the actor performing the action
     * @param map   the game map where the action occurs
     * @return a string describing the outcome of the attack and healing effect
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // 1. Execute the standard attack logic (damage the target)
        String result = super.execute(actor, map);

        // If the attack was successful (you can add logic to check hit), heal the attacker.
        actor.heal(HEAL_AMOUNT);

        return result + String.format("\n%s absorbs the target's essence and heals %d HP!", actor, HEAL_AMOUNT);
    }

    /**
     * Returns a descriptive string for displaying this action in the game menu.
     *
     * @param actor the actor performing this action
     * @return a menu-friendly description of the rage strike action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " unleashes a Lifesteal Rage Strike on " + target + "!";
    }
}

