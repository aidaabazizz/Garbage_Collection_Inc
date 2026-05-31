package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public class IonizedBarrier extends Ground {
    private int lifeSpan = 3; // Lasts for 3 turns

    public IonizedBarrier() {
        super('☵', "Ionized Barrier");
    }

    /**
     * Physics logic: This tile cannot be entered by any actor.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Lifecycle logic: Decrements turns and reverts to normal Floor when expired.
     */
    @Override
    public void tick(Location location) {
        lifeSpan--;
        if (lifeSpan <= 0) {
            location.setGround(new Floor());
        }
    }
}
