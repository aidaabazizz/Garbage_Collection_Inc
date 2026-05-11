package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Elsa's natural ice-based weapon.
 *
 * @author Aida
 */
public class IceBlast extends IntrinsicWeapon {

    public IceBlast(int damage, int hitRate) {
        super(damage, "blasts with ice", hitRate, "ice blast");
    }

    public IceBlast(int damage, int hitRate, String verb) {
        super(damage, verb, hitRate, "ice blast");
    }

    public int getDamageValue() {
        return damage;
    }

    public int getHitRateValue() {
        return hitRate;
    }
}