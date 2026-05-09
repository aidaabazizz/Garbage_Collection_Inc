package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Contract for items that can be sold to the Supercomputer.
 *
 * @author Suchir
 * @version 1.0
 */
public interface Sellable {

    /**
     * Gets the current selling price of the item.
     *
     * @return selling price in credits
     */
    int getSellPrice();

    /**
     * Applies the effect that occurs immediately after selling the item.
     *
     * @param seller the actor selling the item
     * @param map the current game map
     * @param wallet the seller's credit holder
     * @return description of the selling effect
     */
    String soldBy(Actor seller, GameMap map, CreditHolder wallet);
}