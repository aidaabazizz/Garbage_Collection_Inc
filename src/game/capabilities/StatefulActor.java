package game.capabilities;

import game.weapons.CrazyChickenBeak;

/**
 * Interface for actors that can have their state modified by external state machines.
 * This allows states to modify actor behavior without using instanceof checks.
 *
 * @author Aida
 */
public interface StatefulActor {

    /**
     * Replaces the actor's intrinsic weapon with a new one.
     * Used for state transitions that modify combat capabilities.
     *
     * @param beak The new beak weapon to equip
     */
    void setBeak(CrazyChickenBeak beak);

    /**
     * Gets the actor's current beak weapon for inspection.
     * Used to store original stats before modification.
     *
     * @return The current beak weapon
     */
    CrazyChickenBeak getBeak();

    /**
     * Gets the current state name of the actor for display purposes.
     *
     * @return The name of the current state
     */
    String getCurrentStateName();

    /**
     * Sets the current state name.
     *
     * @param stateName The name of the new state
     */
    void setCurrentStateName(String stateName);
}