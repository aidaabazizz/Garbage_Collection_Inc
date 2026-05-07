package game.finance;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.Ability;
import game.enums.ItemStatistics;
import edu.monash.fit2099.engine.items.Item;

/**
 * Represents a worker's credit storage for company transactions.
 * The wallet is stored as a non-portable item in the worker's inventory.
 *
 * @author Suchir
 * @version 1.0
 */
public class Wallet extends Item {
    private static final int MAX_CREDITS = 1000;
    private int credits;

    /**
     * Constructor for Wallet.
     */
    public Wallet() {
        super("Wallet", '$');
        this.credits = 0;
        this.makeNonPortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(0));
        this.enableAbility(Ability.ESSENTIAL);
    }

    /**
     * Adds credits to the wallet without exceeding the maximum limit.
     *
     * @param amount the amount of credits to add
     * @return the actual number of credits added
     */
    public int addCredits(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int current = credits;
        credits = Math.min(MAX_CREDITS, credits + amount);
        return credits - current;
    }

    /**
     * Deducts credits from the wallet if enough credits are available.
     *
     * @param amount the amount of credits to deduct
     * @return true if the deduction succeeds, false otherwise
     */
    public boolean deductCredits(int amount) {
        if (amount <= 0) {
            return true;
        }

        if (credits < amount) {
            return false;
        }

        credits -= amount;
        return true;
    }

    /**
     * Forcefully deducts credits, stopping at zero.
     *
     * @param amount the amount of credits to deduct
     * @return the actual number of credits deducted
     */
    public int forceDeductCredits(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int before = credits;
        credits = Math.max(0, credits - amount);
        return before - credits;
    }

    /**
     * Checks whether the wallet has enough credits.
     *
     * @param amount the required amount
     * @return true if enough credits are available
     */
    public boolean hasEnough(int amount) {
        return credits >= amount;
    }

    /**
     * Gets the current credits.
     *
     * @return current credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Gets the maximum wallet capacity.
     *
     * @return maximum credits
     */
    public int getMaxCredits() {
        return MAX_CREDITS;
    }

    /**
     * Returns the wallet's display text.
     *
     * @return wallet display text
     */
    @Override
    public String toString() {
        return "Wallet (" + credits + "/" + MAX_CREDITS + " credits)";
    }
}