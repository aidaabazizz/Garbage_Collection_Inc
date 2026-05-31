package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ActivateHeavenTokenAction;
import game.sanctuary.SanctuaryTool;

public class HeavenToken extends Item implements SanctuaryTool {
    public HeavenToken() {
        super("Heaven Token", 'ε');
    }

    /**
     * Returns the action to activate this token, available when the actor holds it.
     *
     * @param actor    the actor holding the token
     * @param map      the game map
     * @return list containing the activation action
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ActivateHeavenTokenAction(this));
        return actions;
    }

    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        // Logic to anchor a SanctuaryField object to this location for 10 turns
        return actor + " uses the Heaven Token! A holy field manifests.";
    }
}
