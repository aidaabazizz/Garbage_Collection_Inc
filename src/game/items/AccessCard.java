package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.UnlockDoorAction;
import game.capabilities.Purchasable;
import game.capabilities.Unlockable;
import game.enums.AccessLevel;
import game.enums.ItemStatistics;
import game.capabilities.CreditHolder;
import game.managers.AlarmManager;

import java.util.Random;

/**
 * A security item used to authorize entry through locked doors.
 * The access card has a clearance level and can also be purchased
 * from the Supercomputer.
 *
 * @author Suchir
 * @version 1.0
 */
public class AccessCard extends Item implements Purchasable {

    private static final int LEVEL_ONE_PRICE = 50;
    private static final int LEVEL_TWO_PRICE = 100;
    private static final int LEVEL_THREE_PRICE = 200;

    private static final int LEVEL_ONE_WEIGHT = 1;
    private static final int LEVEL_TWO_WEIGHT = 2;
    private static final int LEVEL_THREE_WEIGHT = 3;

    private static final int LEVEL_TWO_DAMAGE = 5;
    private static final int HIDDEN_FEE = 50;
    private static final int HIDDEN_FEE_CHANCE = 50;

    private final AccessLevel accessLevel;
    private final Random random = new Random();

    /**
     * Constructor for a Level 1 Access Card.
     */
    public AccessCard() {
        this(AccessLevel.LEVEL_ONE);
    }

    /**
     * Constructor for AccessCard.
     *
     * @param accessLevel the clearance level of the card
     */
    public AccessCard(AccessLevel accessLevel) {
        super(getNameFor(accessLevel), getDisplayCharFor(accessLevel));
        this.accessLevel = accessLevel;
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(getWeightFor(accessLevel)));
        this.makePortable();
    }

    /**
     * Gets the access level of this card.
     *
     * @return access level
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Gets the purchase price of the card.
     *
     * @return purchase price
     */
    @Override
    public int getPurchasePrice() {
        return switch (accessLevel) {
            case LEVEL_ONE -> LEVEL_ONE_PRICE;
            case LEVEL_TWO -> LEVEL_TWO_PRICE;
            case LEVEL_THREE -> LEVEL_THREE_PRICE;
        };
    }

    /**
     * Applies the purchase effect of the card.
     *
     * @param buyer the actor buying the item
     * @param map the current game map
     * @param wallet the buyer's wallet
     * @return description of the purchase effect
     */
    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        if (accessLevel == AccessLevel.LEVEL_TWO) {
            buyer.hurt(LEVEL_TWO_DAMAGE);
            return buyer + " takes " + LEVEL_TWO_DAMAGE + " damage from the blood calibration.";
        }

        if (accessLevel == AccessLevel.LEVEL_THREE) {
            if (random.nextInt(100) < HIDDEN_FEE_CHANCE) {
                int deducted = wallet.forceDeductCredits(HIDDEN_FEE);
                return "The Supercomputer applies a hidden fee and deducts "
                        + deducted + " extra credits.";
            }

            return buyer + " purchases an Access Card (Level 3). No hidden fee is applied.";
        }

        return buyer + " purchases an Access Card (Level 1).";
    }

    /**
     * Generates unlock actions for adjacent locked doors if this card has
     * enough access clearance.
     *
     * @param owner the actor carrying the access card
     * @param map the game map containing the actor
     * @return available unlock actions
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

    /**
     * Gets the display name for an access card.
     *
     * @param accessLevel the access level
     * @return display name
     */
    private static String getNameFor(AccessLevel accessLevel) {
        return switch (accessLevel) {
            case LEVEL_ONE -> "Access Card (Level 1)";
            case LEVEL_TWO -> "Access Card (Level 2)";
            case LEVEL_THREE -> "Access Card (Level 3)";
        };
    }

    /**
     * Gets the display character for an access card.
     *
     * @param accessLevel the access level
     * @return display character
     */
    private static char getDisplayCharFor(AccessLevel accessLevel) {
        return switch (accessLevel) {
            case LEVEL_ONE -> '▤';
            case LEVEL_TWO -> 'α';
            case LEVEL_THREE -> '◐';
        };
    }

    /**
     * Gets the weight for an access card.
     *
     * @param accessLevel the access level
     * @return card weight
     */
    private static int getWeightFor(AccessLevel accessLevel) {
        return switch (accessLevel) {
            case LEVEL_ONE -> LEVEL_ONE_WEIGHT;
            case LEVEL_TWO -> LEVEL_TWO_WEIGHT;
            case LEVEL_THREE -> LEVEL_THREE_WEIGHT;
        };
    }
}