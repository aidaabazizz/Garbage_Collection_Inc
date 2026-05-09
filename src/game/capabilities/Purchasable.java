package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Contract for items that can be purchased from the Supercomputer.
 *
 * @author Suchir
 * @version 1.0
 */
public interface Purchasable {

    /**
     * Gets the purchase price of the item.
     *
     * @return purchase price in credits
     */
    int getPurchasePrice();

    /**
     * Applies the effect that occurs immediately after purchasing the item.
     *
     * @param buyer the actor buying the item
     * @param map the current game map
     * @param wallet the buyer's credit holder
     * @return description of the purchase effect
     */
    String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet);

    /**
     * Applies the effect that occurs when the purchase fails.
     *
     * @param buyer the actor attempting to buy the item
     * @param map the current game map
     * @param wallet the buyer's credit holder
     * @return description of the failed purchase effect
     */
    default String failedPurchaseBy(Actor buyer, GameMap map, CreditHolder wallet) {
        return buyer + " does not have enough credits.";
    }
}