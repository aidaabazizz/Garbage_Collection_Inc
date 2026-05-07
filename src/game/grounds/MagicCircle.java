package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.teleportstrategies.MagicCircleStrategy;

public class MagicCircle extends Ground {
    public MagicCircle() {
        super('◎', "Magic Circle");
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new TeleportAction(new MagicCircleStrategy()));
        return actions;
    }
}