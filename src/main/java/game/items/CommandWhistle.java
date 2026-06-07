package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.*;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.UseCommandWhistleAction;
import game.capabilities.CreditHolder;
import game.capabilities.MotivationStatus;
import game.capabilities.Purchasable;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;


public class CommandWhistle extends Item implements SanctuaryTool, Purchasable {

    private static final int PURCHASE_PRICE = 100;
    private static final int WEIGHT = 1;
    /** Maximum tile radius to search for a friendly worker. */
    private static final int SEARCH_RADIUS = 5;


    public CommandWhistle() {
        super("Command Whistle", 'f');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Returns the activation action when this whistle is in the actor's inventory.
     *
     * @param actor    the actor holding the whistle
     * @param map      the game map
     * @return the list of available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseCommandWhistleAction(this));
        return actions;
    }

    /**
     * Implements SanctuaryTool interface. Delegates to UseCommandWhistleAction.
     *
     * @param actor    the actor
     * @param map      the game map
     * @param location the actor's location
     * @return description string
     */
    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        Actor target = findNearestFriendly(actor, location);
        if (target == null) target = actor;

        // 2. Apply the effect
        target.addStatus(new MotivationStatus(location));

        // 3. Consume the item
        actor.getInventory().remove(this);

        return String.format("%s blows the Command Whistle! %s is motivated — an AoE pulse will fire next turn!",
                actor, target);
    }

    /**
     * Scans all exits from the whistle-user's location for a friendly (WORKER) actor.
     * Uses Ability.WORKER capability check — no instanceof (DIP).
     *
     * @param user the actor using the whistle (excluded from search)
     * @param here the whistle-user's location
     * @return the first friendly actor found, or null if none
     */
    private Actor findNearestFriendly(Actor user, Location here) {
        Actor closest = null;
        int minDist = Integer.MAX_VALUE;

        for (int y : here.map().getYRange()) {
            for (int x : here.map().getXRange()) {
                Location loc = here.map().at(x, y);
                if (!loc.containsAnActor()) continue;
                Actor candidate = loc.getActor();
                if (candidate == user) continue;
                if (!candidate.hasAbility(Ability.WORKER)) continue;
                int dist = Math.abs(loc.x() - here.x()) + Math.abs(loc.y() - here.y());
                if (dist <= SEARCH_RADIUS && dist < minDist) {
                    minDist = dist;
                    closest = candidate;
                }
            }
        }
        return closest;
    }

    // --- Purchasable Implementation ---
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE; // Rare item price
    }

    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        wallet.deductCredits(this.getPurchasePrice());
        buyer.getInventory().add(this);
        return buyer + " bought a Command Whistle for " + PURCHASE_PRICE + " credits.";
    }

}