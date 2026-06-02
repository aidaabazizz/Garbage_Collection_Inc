


package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.sanctuary.SanctuaryTool;


public class UseCommandWhistleAction extends Action {
    private final SanctuaryTool tool;

    public UseCommandWhistleAction(SanctuaryTool tool) {
        this.tool = tool;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        // This triggers the physics logic we wrote in CommandWhistle
        return tool.activateSanctuaryEffect(actor, map, map.locationOf(actor));
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " blows the Command Whistle (AoE knockback pulse)";
    }
}