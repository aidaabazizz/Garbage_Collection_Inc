package edu.monash.fit2099.demo.mars.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;


public class Burning implements Status {

    private int duration = 3;
    private final Flammable flammable;

    public Burning(Flammable flammable) {
        this.flammable = flammable;
    }

    @Override
    public void tickStatus(GameEntity gameEntity, Location location) {
        if (flammable != null) {
            flammable.burn(1);
            duration--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return duration == 0;
    }

    @Override
    public String toString() {
        return "Burnable";
    }

}
