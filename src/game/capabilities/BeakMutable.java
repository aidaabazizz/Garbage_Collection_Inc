package game.capabilities;

import game.weapons.CrazyChickenBeak;

/**
 * Represents an actor whose beak weapon can be changed by a state.
 *
 * @author Aida
 * @version 1.0
 */
public interface BeakMutable {
    CrazyChickenBeak getBeak();
    void setBeak(CrazyChickenBeak beak);
}