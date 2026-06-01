package game.capabilities;

/**
 * A contract for objects that can store and manage credits.
 *
 * @author Suchir
 * @version 1.0
 */
public interface CreditHolder {

    /**
     * Adds credits without exceeding the maximum credit limit.
     *
     * @param amount the amount of credits to add
     * @return the actual number of credits added
     */
    int addCredits(int amount);

    /**
     * Deducts credits if enough credits are available.
     *
     * @param amount the amount of credits to deduct
     * @return true if the deduction succeeds, false otherwise
     */
    boolean deductCredits(int amount);

    /**
     * Forcefully deducts credits without going below zero.
     *
     * @param amount the amount of credits to deduct
     * @return the actual number of credits deducted
     */
    int forceDeductCredits(int amount);

    /**
     * Checks whether enough credits are available.
     *
     * @param amount the required amount
     * @return true if enough credits are available
     */
    boolean hasEnough(int amount);

    /**
     * Gets the current number of credits.
     *
     * @return current credits
     */
    int getCredits();

}