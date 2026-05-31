package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

public class RageStrikeAction extends AttackAction{

    public RageStrikeAction(Actor target, String direction, Weapon weapon) {
        super(target, direction, weapon);
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        // 1. Execute the standard attack logic (damage the target)
        String result = super.execute(actor, map);

        // 2. Rule 2: COMPLEX EFFECT (Life Steal)
        // If the attack was successful (you can add logic to check hit), heal the attacker.
        int healAmount = 2;
        actor.heal(healAmount);

        return result + String.format("\n%s absorbs the target's essence and heals %d HP!", actor, healAmount);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unleashes a Lifesteal Rage Strike!";
    }
}

