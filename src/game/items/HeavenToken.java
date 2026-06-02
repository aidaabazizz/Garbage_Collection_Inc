package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.ActivateHeavenTokenAction;
import game.effects.SanctuaryField;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;

public class HeavenToken extends Item implements SanctuaryTool {
    public HeavenToken() {
        super("Heaven Token", 'ε');
        this.makePortable(); // FIX: Allows you to pick it up
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
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
        Ground previousGround = location.getGround();

        // Replace the floor Bob is standing on with a Sanctuary Field
        location.setGround(new SanctuaryField(10, previousGround));

        // Remove from inventory
        actor.getInventory().remove(this);

        return "\u001B[35m" + actor + " activates the Heaven Token! A protective field manifests!\u001B[0m";
    }

}
