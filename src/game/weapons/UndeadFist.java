package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A specialized intrinsic weapon representing the bare fists of an Undead entity.
 *
 * @author Jewell Gomes
 */
public class UndeadFist extends IntrinsicWeapon {
    /**
     * Constructor for the UndeadFist.
     * @param damage the amount of damage dealt by the punch.
     * @param hitRate the percentage chance for the attack to land.
     */
    public UndeadFist(int damage, int hitRate) {
        super(damage, "punches", hitRate, "bare fist");
    }
}