package game.capabilities;

import edu.monash.fit2099.engine.positions.Location;

//req4
public interface Infectable {
    /**
     * What happens the exact moment the parasite touches the target.
     * (e.g., Undead dies, Worker/Item adds the InfectedStatus).
     */
    void reactToInfection(Location location);

    /**
     * What happens every turn while the infection is active.
     * (e.g., Worker spawns parasite every 5 turns, Lantern drains oil).
     */
    void updateInfection(Location location);

}
