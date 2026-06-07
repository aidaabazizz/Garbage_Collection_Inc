package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.ActivateHeavenTokenAction;
import game.capabilities.CreditHolder;
import game.capabilities.Purchasable;
import game.grounds.SanctuaryField;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;

public class HeavenToken extends Item implements SanctuaryTool, Purchasable {

    private static final int WEIGHT = 1;
    private static final int FIELD_DURATION = 10;
    private static final int PURCHASE_PRICE = 50;

    public HeavenToken() {
        super("Heaven Token", 'ε');
        this.makePortable(); // FIX: Allows you to pick it up
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Returns the action to activate this token, available when the actor holds it.
     *
     * @param actor    the actor holding the token
     * @param map      the game map
     * @return list containing the activation action
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ActivateHeavenTokenAction(this));
        return actions;
    }

    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        Ground previousGround = location.getGround();

        // Replace the floor Bob is standing on with a Sanctuary Field
        location.setGround(new SanctuaryField(FIELD_DURATION, previousGround));

        // Remove from inventory
        actor.getInventory().remove(this);

        // 4. Return the result string to be displayed in the console
        return String.format("\u001B[35m%s activates the Heaven Token! A holy field manifests for %d turns!\u001B[0m",
                actor, FIELD_DURATION);

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
        return buyer + " bought a Heaven Token for " + PURCHASE_PRICE + " credits.";
    }

}
