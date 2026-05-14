package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.UnlockDoorAction;
import game.capabilities.CreditHolder;
import game.capabilities.Purchasable;
import game.capabilities.Unlockable;
import game.enums.AccessLevel;
import game.enums.ItemStatistics;
import game.managers.AlarmManager;

import java.util.Random;

/**
 * A security access card used by workers to unlock doors with matching or lower
 * clearance requirements.
 * <p>
 * The card stores an {@link AccessLevel}, which defines its clearance rank,
 * display character, purchase price, and weight. This avoids creating separate
 * card classes for each level while still allowing different cards to have
 * different values and purchase effects.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public class AccessCard extends Item implements Purchasable {

    private static final int LEVEL_TWO_DAMAGE = 5;
    private static final int HIDDEN_FEE = 50;
    private static final int HIDDEN_FEE_CHANCE = 50;
    private static final int PERCENTAGE_BOUND = 100;

    private final AccessLevel accessLevel;
    private final Random random = new Random();

    /**
     * Constructor for a Level 1 Access Card.
     * <p>
     * This is used for the starting access card because the initial card in the
     * game is a Level 1 card.
     * </p>
     */
    public AccessCard() {
        this(AccessLevel.LEVEL_ONE);
    }

    /**
     * Constructor for an Access Card with a specified clearance level.
     *
     * @param accessLevel the clearance level of this access card
     */
    public AccessCard(AccessLevel accessLevel) {
        super(accessLevel.getCardName(), accessLevel.getDisplayChar());
        this.accessLevel = accessLevel;
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(accessLevel.getWeight()));
        this.makePortable();
    }

    /**
     * Gets the clearance level of this access card.
     *
     * @return the access level of this card
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Gets the purchase price of this access card.
     *
     * @return the purchase price in credits
     */
    @Override
    public int getPurchasePrice() {
        return accessLevel.getPurchasePrice();
    }

    /**
     * Applies the immediate effect that occurs after this access card is purchased.
     * <p>
     * Level 1 has no harmful purchase effect. Level 2 deals 5 damage to the buyer
     * due to blood calibration. Level 3 has a 50% chance to deduct an additional
     * 50 credits as a hidden fee.
     * </p>
     *
     * @param buyer the actor buying this access card
     * @param map the current game map
     * @param wallet the buyer's credit holder
     * @return a description of the purchase effect
     */
    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        if (accessLevel == AccessLevel.LEVEL_TWO) {
            buyer.hurt(LEVEL_TWO_DAMAGE);
            return buyer + " takes " + LEVEL_TWO_DAMAGE + " damage from the blood calibration.";
        }

        if (accessLevel == AccessLevel.LEVEL_THREE) {
            if (random.nextInt(PERCENTAGE_BOUND) < HIDDEN_FEE_CHANCE) {
                int deducted = wallet.forceDeductCredits(HIDDEN_FEE);
                return "The Supercomputer applies a hidden fee and deducts "
                        + deducted + " extra credits.";
            }

            return buyer + " avoids the hidden fee.";
        }

        return buyer + " purchases a basic Level 1 access card.";
    }

    /**
     * Generates unlock actions for adjacent locked doors when this card has enough
     * clearance to open them.
     * <p>
     * This method checks adjacent locations for grounds that implement
     * {@link Unlockable}. If the door is locked and this card's access level can
     * open the door's required level, an {@link UnlockDoorAction} is added.
     * </p>
     *
     * @param owner the actor carrying this access card
     * @param map the game map containing the actor
     * @return a list of available unlock actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (AlarmManager.getInstance().isActive()) {
            return actions;
        }

        Location currentLocation = map.locationOf(owner);

        for (Exit exit : currentLocation.getExits()) {
            Location destination = exit.getDestination();
            Unlockable target = destination.getGroundAs(Unlockable.class);

            if (target != null
                    && !target.isUnlocked()
                    && accessLevel.canOpen(target.getRequiredAccessLevel())) {
                actions.add(new UnlockDoorAction(target, exit.getName()));
            }
        }

        return actions;
    }
}