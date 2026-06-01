package edu.monash.fit2099.engine.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Interface for weapon items.
 * Since an actor may decide to attack its target using its intrinsic weapon or a weapon item,
 * some actions may accept an instance of Weapon instead of a WeaponItem or an IntrinsicWeapon
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public interface Weapon {

    /**
     * This method is used to define how the weapon attacks its target
     * For example, some weapons deals fire damage when the target is attacked.
     * This method can also be used to determine whether the attack lands (e.g.
     * whether the chance to hit is met), the weapon's damage (e.g. Dragonslayer
     * greatsword deals 100 damage to its target), and the weapon's verb (e.g. slashes,
     * pierces, etc.)
     *
     * @param attacker the actor who performed the attack
     * @param target   the actor who is the target of the attack
     * @param map      the map on which the attack was executed
     * @return the description of what the weapon did when the attack was performed
     */
    String attack(Actor attacker, Actor target, GameMap map);

}
