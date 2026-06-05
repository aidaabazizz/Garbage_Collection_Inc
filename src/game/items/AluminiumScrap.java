package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.Depositable;
import game.enums.ItemStatistics;
import game.managers.QuotaManager;
import java.util.Random;

/**
 * A piece of aluminium scrap that can be deposited into the Super Computer
 * for Company Credits.
 * Depositing the scrap has a chance of injuring the worker due to its
 * sharp edges.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class AluminiumScrap extends Item implements Depositable {
    /**
     * Weight of the scrap.
     */
    private static final int WEIGHT = 2;

    /**
     * Company Credits earned when deposited.
     */
    private static final int COMPANY_CREDITS = 50;

    /**
     * Damage dealt when the worker is injured.
     */
    private static final int CUT_DAMAGE = 5;

    /**
     * Percentage chance of taking damage when depositing.
     */
    private static final int DAMAGE_CHANCE = 20;

    /**
     * Random number generator used for deposit effects.
     */
    private final Random rand = new Random();

    /**
     * Creates an Aluminium Scrap item.
     */
    public AluminiumScrap() {
        super("Aluminium Scrap", '%');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    /**
     * Returns the Company Credit value of the scrap.
     * @return the Company Credit value
     */
    @Override
    public int getCompanyCreditValue() {
        return COMPANY_CREDITS;
    }

    /**
     * Deposits the aluminium scrap into the Super Computer.
     * There is a chance that the worker takes damage from the
     * scrap's sharp edges.
     * @param depositor the actor depositing the scrap
     * @param map the map the actor is on
     * @param quotaManager the quota manager handling Company Credits
     * @return the result of the deposit
     */
    @Override
    public String depositBy(Actor depositor, GameMap map, QuotaManager quotaManager) {
        if (rand.nextInt(100) < DAMAGE_CHANCE) {
            depositor.hurt(CUT_DAMAGE);
            return "Jagged metal slices the worker's hands. "
                    + depositor + " takes " + CUT_DAMAGE + " damage!";
        }
        return "The scrap is being deposited safely.";
    }
}