package game.utils;

/**
 * Tracks whether a consumable item was used so Elsa can react with ICE_SPIKE.
 *
 * @author Aida
 * @version 1.0
 */
public class ConsumableUseTracker {
    private static boolean consumedThisRound = false;

    public static void markConsumed() {
        consumedThisRound = true;
    }

    public static boolean consumeFlag() {
        if (consumedThisRound) {
            consumedThisRound = false;
            return true;
        }
        return false;
    }
}