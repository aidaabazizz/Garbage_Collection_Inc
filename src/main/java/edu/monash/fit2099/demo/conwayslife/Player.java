package edu.monash.fit2099.demo.conwayslife;

import edu.monash.fit2099.demo.forest.BasicInventory;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.displays.Menu;

/**
 * Class representing the Player.
 */
public class Player extends Actor {

    /**
     * Constructor.
     *
     * @param name        Name to call the player in the UI
     */
    public Player(String name) {
        super(name, '@',1000, new BasicInventory());

    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (lastAction.getNextAction() != null)
            return lastAction.getNextAction();
        Menu menu = new Menu(actions);
        actions.add(new SleepAction());
        return menu.showMenu(this, display);
    }

}
