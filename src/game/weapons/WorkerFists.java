package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A concrete implementation of the worker's bare hands.
 */
public class WorkerFists extends IntrinsicWeapon {
    public WorkerFists() {
        // damage: 2, verb: "punches", hitRate: 100, name: "fist"
        super(2, "punches", 100, "fist");
    }
}