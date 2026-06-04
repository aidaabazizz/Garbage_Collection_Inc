package edu.monash.fit2099.demo.mars.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

public class Wall extends Ground {

    public Wall() {
        super('#', "Wall");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public boolean blocksThrownObjects() {
        return true;
    }
}
