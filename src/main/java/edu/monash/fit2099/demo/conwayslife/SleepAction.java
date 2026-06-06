package edu.monash.fit2099.demo.conwayslife;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public class SleepAction extends Action {
    private int sleepTime = 200;

    @Override
    public String execute(Actor actor, GameMap map) {
        sleepTime--;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return actor + " is sleeping.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Sleep a while";
    }

    @Override
    public Action getNextAction() {
        if (sleepTime > 0)
            return this;
        return null;
    }

}
