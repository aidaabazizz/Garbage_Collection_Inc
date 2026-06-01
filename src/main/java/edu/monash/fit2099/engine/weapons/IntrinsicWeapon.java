package edu.monash.fit2099.engine.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Random;

/**
 * Class that represents a weapon for an unarmed Actor (e.g. fists, claws, etc.)
 * Since the attack is done without the use of any item, e.g., damage is dealt by punching
 * the target using the actor's bare fist, it is semantically incorrect to make a natural weapon
 * an item.
 *
 * @author Riordan Alfredo
 * @see Weapon
 */
public abstract class IntrinsicWeapon implements Weapon {

    protected int damage;
    protected String verb;
    protected int hitRate;
    protected String name;

    /**
     * Constructor
     *
     * @param damage damage to health
     * @param verb   a word that will be printed in the action, e.g. "punches"
     * @param hitRate the intrinsic weapon's chance to hit
     * @param name   the name of the intrinsic weapon, e.g. "fist", "claw"
     */
    public IntrinsicWeapon(int damage, String verb, int hitRate, String name) {
        this.damage = damage;
        this.verb = verb;
        this.hitRate = hitRate;
        this.name = name;
    }

    /**
     * Sample implementation of the attack method of the intrinsic weapon.
     * If the hit rate is not met, the attacker misses the target.
     * Otherwise, the target is hit, hurting the target by the intrinsic weapon's damage
     *
     * @param attacker the actor who performed the attack
     * @param target   the actor who is the target of the attack
     * @param map      the map on which the attack was executed
     * @return the description once the attack is done
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        Random rand = new Random();
        if (!(rand.nextInt(100) <= this.hitRate)) {
            return attacker + " misses " + target + ".";
        }
        target.hurt(damage);
        return String.format("%s %s %s for %d damage", attacker, verb, target, damage);
    }

    @Override
    public String toString() {
        return name;
    }
}
