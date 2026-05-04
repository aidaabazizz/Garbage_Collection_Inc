package game.finance;

/**
 * Represents a worker's credit storage for company transactions.
 * The wallet enforces the company credit limit of 1000 credits.
 *
 * @author Suchir
 * @version 1.0
 */
public class Wallet {
    private static final int MAX_CREDITS = 1000;
    private int credits;

    /**
     * Constructor for Wallet.
     */
    public Wallet() {
        this.credits = 0;
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
}