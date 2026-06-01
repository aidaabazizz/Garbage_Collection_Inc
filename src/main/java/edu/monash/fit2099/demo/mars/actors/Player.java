package edu.monash.fit2099.demo.mars.actors;

import edu.monash.fit2099.demo.mars.capabilities.*;
import edu.monash.fit2099.demo.mars.items.BasicInventory;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Class representing the Player.
 */
public final class Player extends Actor implements Flammable {

    /**
     * Constructor.
     *
     * @param name Name to call the player in the UI
     * @param hitPoints Player's starting number of hitpoints
     */
    public Player(String name, int hitPoints) {
        super(name, '@', hitPoints, new BasicInventory());

    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Check if it has previous action or not. If so, execute that last action.
        // Useful when you want to implement chain-actions like sleeping implementation.
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }
        Menu menu = new Menu(actions);

        return menu.showMenu(this, display);
    }



    @Override
    public void burn(int damage) {
        this.hurt(damage);
    }

}
