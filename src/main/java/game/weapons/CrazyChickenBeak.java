package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The natural weapon of the CrazyChicken.
 * A sharp beak used for pecking attacks.
 *
 * @author Aida
 */
public class CrazyChickenBeak extends IntrinsicWeapon {

    /**
     * Constructor for the CrazyChickenBeak with default verb.
     * @param damage the amount of damage dealt by the peck
     * @param hitRate the percentage chance for the attack to land
     */
    public CrazyChickenBeak(int damage, int hitRate) {
        super(damage, "pecks", hitRate, "sharp beak");
    }

    /**
     * Constructor with custom verb.
     */
    public CrazyChickenBeak(int damage, int hitRate, String verb) {
        super(damage, verb, hitRate, "sharp beak");
    }

    /**
     * Get the damage value (access to protected field).
     */
    public int getDamageValue() {
        return damage;
    }

    /**
     * Get the hit rate value (access to protected field).
     */
    public int getHitRateValue() {
        return hitRate;
    }
}