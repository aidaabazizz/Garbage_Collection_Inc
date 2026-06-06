package game.managers;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * Manages global corporate quota cycles, company ranking tiers, and deadlines.
 * The game starts at Company Rank 1 (Supercomputer) with a base quota of 100 Company Credits
 * and a time limit of 200 game turns.
 * If the quota is met, the quota will be reset and increased by 5%, and the time limit will be reset and increased by 10%.
 * However, if it is not met, the supercomputer will fire workers nearby and for those workers
 * that is not nearby, they will no longer have access to any facilities provided by supercomputer.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class QuotaManager {

    /**
     * Starting configuration constants for the quota system.
     */
    private static final int STARTING_RANK = 1;
    /**
     * Initial quota target required to progress from rank 1.
     */
    private static final int STARTING_QUOTA_TARGET = 100;

    /**
     * Initial maximum time limit (in turns) for completing the quota cycle.
     */
    private static final int STARTING_MAX_TIME_LIMIT = 3;

    /**
     * Current company rank in the quota system.
     */
    private int companyRank;

    /**
     * Current quota target that must be met to progress to the next rank.
     */
    private int currentQuotaTarget;
    /**
     * Accumulated company credits collected during the current cycle.
     */
    private int accumulatedCompanyCredits;

    /**
     * Maximum time limit (in turns) for the current quota cycle.
     */
    private int currentMaxTimeLimit;

    /**
     * Remaining turns before the quota deadline is reached.
     */
    private int remainingTurns;

    /**
     * Indicates whether facility access (e.g., Supercomputer services) is active.
     */
    private boolean isFacilityAccessActive;

    /**
     * Constructs a QuotaManager with default starting values.
     * Rank starts at 1, quota is 100, and time limit is 200 turns.
     */
    public QuotaManager() {
        this.companyRank = STARTING_RANK;
        this.currentQuotaTarget = STARTING_QUOTA_TARGET;
        this.accumulatedCompanyCredits = 0;
        this.currentMaxTimeLimit = STARTING_MAX_TIME_LIMIT;
        this.remainingTurns = STARTING_MAX_TIME_LIMIT;
        this.isFacilityAccessActive = true;
    }

    /**
     * Returns the current quota target for the cycle.
     */
    public int getCurrentQuotaTarget() {
        return currentQuotaTarget;
    }

    /**
     * Returns the accumulated company credits for the current cycle.
     */
    public int getAccumulatedCompanyCredits() {
        return accumulatedCompanyCredits;
    }

    /**
     * Returns the remaining turns before the quota deadline.
     */
    public int getRemainingTurns() {
        return remainingTurns;
    }

    /**
     * Returns whether facility access is currently active.
     */
    public boolean isFacilityAccessActive() {
        return isFacilityAccessActive;
    }

    /**
     * Adds company credits towards the current quota.
     * @param credits amount of company credits to add
     * @return message describing the update
     */
    public String addCompanyCredits(int credits) {
        this.accumulatedCompanyCredits += credits;

        return "[COMPANY UPDATE] Added " + credits
                + " Company Credits. Total: "
                + accumulatedCompanyCredits + "/"
                + currentQuotaTarget;
    }

    /**
     * Advances the quota cycle by one turn.
     * @param map current game map
     * @return messages generated during this turn, or an empty string if nothing occurred
     */
    public String tickTurnCycle(GameMap map) {
        StringBuilder message = new StringBuilder();

        if (remainingTurns > 0) {
            remainingTurns--;
        }

        if (remainingTurns == 0) {
            if (accumulatedCompanyCredits >= currentQuotaTarget) {
                companyRank++;
                accumulatedCompanyCredits = 0;
                currentQuotaTarget =
                        (int) Math.ceil(currentQuotaTarget * 1.05);
                currentMaxTimeLimit =
                        (int) Math.ceil(currentMaxTimeLimit * 1.10);
                remainingTurns = currentMaxTimeLimit;
                message.append("[QUOTA SUCCESS] Promoted to Company Rank ")
                        .append(companyRank)
                        .append("! New Quota Target: ")
                        .append(currentQuotaTarget)
                        .append(" Credits. Remaining Turns: ")
                        .append(remainingTurns);
            } else if (isFacilityAccessActive) {
                message.append("[QUOTA FAILURE] The quota of ")
                        .append(currentQuotaTarget)
                        .append(" Company Credits was not reached before the deadline. ")
                        .append("Workers adjacent to the Supercomputer will be unconscious and facility access has been revoked.");
                message.append(executeCorporateTermination(map));
                isFacilityAccessActive = false;
            }
        }
        return message.toString();
    }

    /**
     * Fires all workers adjacent to the Supercomputer after a quota failure.
     *
     * @param map current game map
     * @return messages describing the workers that were fired
     */
    private String executeCorporateTermination(GameMap map) {
        StringBuilder message = new StringBuilder();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);
                if (location.getGround() != null
                        && location.getGround().toString().contains("Supercomputer")) {
                    for (Exit exit : location.getExits()) {
                        Location adjacentLocation = exit.getDestination();
                        if (adjacentLocation.containsAnActor()) {
                            Actor targetWorker = adjacentLocation.getActor();
                            if (targetWorker.hasAbility(Ability.WORKER)) {
                                int currentHp =
                                        targetWorker.getStatistic(ActorStatistics.HEALTH);
                                targetWorker.hurt(currentHp);
                                message.append(targetWorker)
                                        .append(" is experiencing fire effect and is unconscious!");
                            }
                        }
                    }
                }
            }
        }
        return message.toString();
    }
}