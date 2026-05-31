package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.UseCommandWhistleAction;
import game.capabilities.MotivationStatus;
import game.sanctuary.SanctuaryTool;



public class CommandWhistle extends Item implements SanctuaryTool{
    public CommandWhistle() {
        super("Command Whistle", 'f');
        this.makePortable();
    }

    /**
     * Returns the activation action when this whistle is in the actor's inventory.
     *
     * @return the list of available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseCommandWhistleAction(this));
        return actions;
    }

    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        return new UseCommandWhistleAction(this).execute(actor, map);
    }



}
