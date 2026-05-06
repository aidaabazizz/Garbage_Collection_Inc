package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Infectable;

//req4
public class InfectAction extends Action {
    private final Infectable target;

    public InfectAction(Infectable target) {
        this.target = target;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        target.reactToInfection(map.locationOf(actor));
        map.removeActor(actor); // Parasite dies immediately
        return actor + " has infected a host and perished.";
    }

    @Override
    public String menuDescription(Actor actor) { return ""; }
}


