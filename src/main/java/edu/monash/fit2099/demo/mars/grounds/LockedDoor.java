package edu.monash.fit2099.demo.mars.grounds;

import edu.monash.fit2099.demo.mars.actions.WindowSmashAction;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;


public class LockedDoor extends Ground {

    public LockedDoor() {
        super('+', "Locked Door");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        return new ActionList(new WindowSmashAction(direction, location));
    }

    @Override
    public boolean blocksThrownObjects() {
        return true;
    }
}
