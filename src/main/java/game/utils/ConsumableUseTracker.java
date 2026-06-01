package game.utils;

import edu.monash.fit2099.engine.actors.Actor;
import game.enums.Ability;

/**
 * Tracks whether the PLAYER (ContractedWorker) consumed a consumable item.
 * Used by Elsa to react with ICE_SPIKE state.
 * Only tracks player consumption - NPC consumption (slimes, chickens, etc.)
 * does NOT trigger ICE_SPIKE.
 *
 * @author Aida
 * @version 2.0
 */
public class ConsumableUseTracker {

    private static boolean playerConsumedThisRound = false;

    /**
     * Marks that an actor has consumed a consumable item this round.
     * Only triggers the tracker if the actor is a player (has WORKER ability).
     * Uses hasAbility() - NO instanceof!
     *
     * @param actor The actor who consumed the item
     */
    public static void markConsumed(Actor actor) {
        // Uses capability pattern - NO instanceof!
        if (actor.hasAbility(Ability.WORKER)) {
            playerConsumedThisRound = true;
        }
    }

    /**
     * Checks if the player consumed a consumable this round.
     * Returns true only once per consumption, then resets.
     *
     * @return true if player consumed this round, false otherwise
     */
    public static boolean consumeFlag() {
        if (playerConsumedThisRound) {
            playerConsumedThisRound = false;
            return true;
        }
        return false;
    }
}