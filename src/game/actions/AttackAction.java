package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.highvoltage.ParalyzedStatus;
import game.highvoltage.ShockedStatus;

/**
 * Special Action for performing an attack on another Actor.
 * This class facilitates combat between entities on the moon facility. In the context
 * of Requirement 3, it is primarily used by the Undead to attack workers using
 * their intrinsic weapons (bare fists).
 *
 * @author Jewell Gomes
 */
public class AttackAction extends Action {
    private Actor target;
    private String direction;
    private Weapon weapon;

    /**
     * Constructor to create an AttackAction.
     *
     * @param target    The Actor to be attacked.
     * @param direction The direction of the target.
     * @param weapon The weapon used for the attack.
     */
    public AttackAction(Actor target, String direction, Weapon weapon) {
        this.target = target;
        this.direction = direction;
        this.weapon = weapon;
    }

    /**
     * Executes the attack logic.
     * This method retrieves the attacker's intrinsic weapon
     * and performs the attack. If the attack results in the target losing all health points,
     * the target's unconscious logic is triggered to handle removal from the map.
     *
     * @param actor The Actor performing the attack.
     * @param map   The GameMap the attack is occurring on.
     * @return A string describing the result of the attack.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = weapon.attack(actor, target, map);

        // If the target is paralyzed, they act as a "Reflective Surface".
        // The attacker takes damage from the "Thorns" effect of the target's suit.
        // The kinetic energy of the hit triggers a secondary status on the attacker.
        if (isTargetConductive(target)) {
            processReflectiveSurge(actor);
            result += String.format("\n\u001B[31m⚡ Electricity arcs back from %s's suit! %s takes 1 damage and is SHOCKED!\u001B[0m",
                    target, actor);
        }


        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }
        return result;
    }

    /**
     * Helper method to check if the target has an active ParalyzedStatus.
     * This isolates the "Status Parsing" logic from the "Action Execution" logic.
     */
    private boolean isTargetConductive(Actor target) {
        for (Status s : target.statuses()) {
            if (s.getClass() == ParalyzedStatus.class && s.isStatusActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Logic for what happens to an attacker when they hit a paralyzed target.
     */
    private void processReflectiveSurge(Actor attacker) {
        attacker.hurt(1);
        attacker.addStatus(new ShockedStatus(2));
    }

    /**
     * Provides a description of the attack for the user interface menu.
     *
     * @param actor The Actor performing the action.
     * @return A string representation of the action.
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction;
    }
}