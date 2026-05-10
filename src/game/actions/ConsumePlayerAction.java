package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Action for slimes to consume players when Elsa is singing.
 *
 * @author Aida
 */
public class ConsumePlayerAction extends Action {
    private final Actor target;
    private final String direction;
    private static final int DAMAGE = 5;

    public ConsumePlayerAction(Actor target, String direction) {
        this.target = target;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        target.hurt(DAMAGE);
        String result = actor + " consumes " + target + " at " + direction + " for " + DAMAGE + " damage!";

        if (!target.isConscious()) {
            result += "\n" + target + " has been consumed!";
        }
        return result;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes " + target + " at " + direction;
    }
}