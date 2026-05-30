package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Represents the high-voltage discharge of a Static Stalker.
 * Follows the same design pattern as IceBlast and UndeadFist.
 *
 * @author [Your Name]
 * @version 1.0
 */
public class GalvanicStrike extends IntrinsicWeapon {

    /**
     * Constructor for GalvanicStrike.
     * @param damage the amount of damage dealt (Page 30: 5)
     * @param hitRate the percentage chance to land (Page 30: 75)
     */
    public GalvanicStrike(int damage, int hitRate) {
        super(damage, "zaps", hitRate, "galvanic discharge");
    }
}