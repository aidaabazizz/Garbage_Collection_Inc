package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.Depositable;
import game.enums.ItemStatistics;
import game.managers.QuotaManager;
import java.util.Random;

public class AluminiumScrap extends Item implements Depositable {
    private static final int WEIGHT = 2;
    private static final int COMPANY_CREDITS = 50;
    private static final int CUT_DAMAGE = 5;
    private static final int DAMAGE_CHANCE = 20;
    private final Random rand = new Random();

    public AluminiumScrap() {
        super("Aluminium Scrap", '%');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    @Override
    public int getCompanyCreditValue() {
        return COMPANY_CREDITS;
    }

    @Override
    public String depositBy(Actor depositor, GameMap map, QuotaManager quotaManager) {
        if (rand.nextInt(100) < DAMAGE_CHANCE) {
            depositor.hurt(CUT_DAMAGE);
            return "Jagged metal slices the worker's hands. " + depositor + " takes " + CUT_DAMAGE + " damage!";
        }
        return "The scrap is being deposited safely.";
    }
}
