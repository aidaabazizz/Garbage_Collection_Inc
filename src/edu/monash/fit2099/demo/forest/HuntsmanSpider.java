package edu.monash.fit2099.demo.forest;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Map;
import java.util.TreeMap;

public class HuntsmanSpider extends Actor {
    private Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    public HuntsmanSpider() {
        super("Huntsman Spider", '8', 1, new BasicInventory());
        this.behaviours.put(999, new WanderBehaviour());
    }

    /**
     * At each turn, select a valid action to perform.
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the valid action that can be performed in that iteration or null if no valid action is found
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if(action != null)
                return action;
        }
        return new DoNothingAction();
    }

    /**
     * The huntsman spider can be attacked by any actor that has the CAN_ATTACK ability
     *
     * @param otherActor the Actor that might be performing attack
     * @param direction  String representing the direction of the other Actor
     * @param map        current GameMap
     * @return list of available actions.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();
        if(otherActor.hasAbility(Abilities.CAN_ATTACK)){
            actions.add(new AttackAction(this, direction));
        }
        return actions;
    }

}