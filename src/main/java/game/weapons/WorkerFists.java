package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A concrete implementation of an actor's bare hands used for combat.
 *
 * This class defines the intrinsic weapon properties specifically for workers,
 * representing their basic punch attack. It serves as the default combat
 * method when a worker character is not holding any external weaponry.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class WorkerFists extends IntrinsicWeapon {
    /**
     * Initializes a new instance of worker fists with specified combat values.
     *
     * The first parameter defines the base damage dealt by a punch, while
     * the second parameter defines the accuracy percentage or hit rate.
     * It sets the default action verb to "punches" and the weapon name to "fist".
     */
    public WorkerFists(int damage,int hitRate) {
        super(damage, "punches", hitRate, "fist");
    }
}