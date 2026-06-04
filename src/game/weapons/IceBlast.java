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
}