package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.behaviours.AttackBehaviour;
import game.behaviours.StealResourceBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Infectable;
import game.capabilities.InfectionStatus;
import game.inventory.BasicInventory;
import game.weapons.UndeadFist;

/**
 * A resource-hoarding creature that steals depositable items from the ground.
 * When infected, loses hoarding ability and becomes aggressive toward workers.
 * Behaviours:
 * - StealResourceBehaviour (priority 1): steals Sellable items from ground
 * - WanderBehaviour (priority 999): moves randomly when nothing to steal
 * When infected:
 * - Removes StealResourceBehaviour
 * - Adds AttackBehaviour (priority 10)
 * - Takes 1 damage per turn
 *
 * @author Aida
 */
public class ScrapSnatcher extends NonPlayerCharacter implements Infectable {
    private static final int INITIAL_HEALTH = 25;

    private static final int WANDER_PRIORITY = 999;
    private static final int STEAL_PRIORITY = 1;
    private static final int ATTACK_PRIORITY = 10;

    private static final int INFECTED_DAMAGE_PER_TURN = 1;

    private static final int PUNCH_DAMAGE = 1;
    private static final int PUNCH_HIT_RATE = 10;

    private boolean isInfected = false;
    private int infectionDamageCounter = 0;

    public ScrapSnatcher() {
        super("Scrap Snatcher", 's', INITIAL_HEALTH, new BasicInventory());
        this.behaviours.put(STEAL_PRIORITY, new StealResourceBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return new UndeadFist(PUNCH_DAMAGE, PUNCH_HIT_RATE);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Handle infection damage over time
        if (isInfected) {
            infectionDamageCounter++;
            if (infectionDamageCounter >= 1) {
                this.hurt(INFECTED_DAMAGE_PER_TURN);
                infectionDamageCounter = 0;
                display.println(this + " takes " + INFECTED_DAMAGE_PER_TURN + " damage from infection!");

                if (!this.isConscious()) {
                    String deathMessage = this.unconscious(map);
                    display.println(deathMessage);
                    return null;
                }
            }
        }

        return super.playTurn(actions, lastAction, map, display);
    }

    @Override
    public void reactToInfection(Location location) {
        this.isInfected = true;
        this.addStatus(new InfectionStatus());

        // Remove stealing behaviour, add attack behaviour
        this.behaviours.remove(STEAL_PRIORITY);
        this.behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());

        new Display().println(this + " has been infected and becomes rabid!");
    }

    @Override
    public void updateInfection(Location location) {
        // Infection damage handled in playTurn
    }

    /**
     * Checks if this ScrapSnatcher is currently infected.
     * Used for testing and tracking infection state.
     *
     * @return true if infected, false otherwise
     */
    public boolean isInfected() {
        return isInfected;
    }
}