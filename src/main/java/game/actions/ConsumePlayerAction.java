package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Action for hypnotized slimes to swallow adjacent workers.
 * If the worker has more than 3 HP, they are spat out with 1 HP.
 * If the worker has 3 HP or less, they are knocked out.
 *
 * @author Aida
 * @version 1.0
 */
public class ConsumePlayerAction extends Action {
    private final Actor target;
    private final String direction;

    public ConsumePlayerAction(Actor target, String direction) {
        this.target = target;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        int currentHealth = target.getStatistic(ActorStatistics.HEALTH);

        if (currentHealth > 3) {
            target.hurt(currentHealth - 1);
            return String.format("\u001B[35m %s swallows %s at %s and spits them out with 1 HP left! \u001B[0m",
                    actor, target, direction);
        }

        target.hurt(currentHealth);
        String result = String.format("\u001B[31m %s DEVOURS %s at %s! The worker has been consumed! \u001B[0m",
                actor, target, direction);

        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        return result;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " swallows " + target + " at " + direction;
    }
}