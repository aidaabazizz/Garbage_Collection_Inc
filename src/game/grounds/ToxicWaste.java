package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public class ToxicWaste extends Ground {

    private final static int HURT = 1;

    public ToxicWaste() {
        super('≈', "Toxic Waste");
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.hurt(HURT);
        }
    }
}
