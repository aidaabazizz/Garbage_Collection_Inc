package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A specialized intrinsic weapon representing the bioelectrical discharge of a Static Stalker.
 *
 * This class is a supporting component of Requirement 3 (High-Voltage Galvanic System).
 * It provides the combat mechanics for the predator, namely the StaticStalker, which evolved
 * from the DormantStaticCreature maintaining thematic consistency within the galvanic ecosystem
 * by utilizing specific electrical terminology for attack descriptions.
 *
 * Design Consistency:
 * This class follows the Monash engine's pattern for natural NPC weapons, similar to
 * Assignment 2's UndeadFist, but with significantly higher damage and a distinct
 * electrical "verb" to provide clear feedback to the player.
 *
 * @author Jewell Gomes
 */
public class GalvanicStrike extends IntrinsicWeapon {

    /**
     * Constructor for the GalvanicStrike.
     *
     * As per the technical requirements (PDF Page 30/34):
     * - Damage: 5 (Greater threat level than standard Undead).
     * - Verb: "zaps" (Visual/Textual feedback of electrical energy).
     * - Hit Rate: 75% (High accuracy representing a kinetic discharge).
     * - Special: "galvanic discharge" (The description of the attack event).
     *
     * @param damage  The amount of health points removed from the target on a successful hit.
     * @param hitRate The percentage probability (0-100) of the attack connecting with the target.
     */
    public GalvanicStrike(int damage, int hitRate) {
        super(damage, "zaps", hitRate, "galvanic discharge");
    }
}