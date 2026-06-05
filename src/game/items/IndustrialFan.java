package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.CreditHolder;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.enums.ItemStatistics;
import game.managers.QuotaManager;
import game.managers.Spawner;

public class IndustrialFan extends Item implements Sellable, Depositable {

    private static final int WEIGHT = 5;
    private static final int SELL_PRICE = 150;
    private static final int COMPANY_CREDITS = 10;
    private static final int HEAL_AMOUNT = 10;
    private final Spawner spawner;

    public IndustrialFan(Spawner spawner) {
        super("Industrial Fan", '@');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
        this.spawner = spawner;
    }

    @Override
    public int getSellPrice() { return SELL_PRICE; }

    @Override
    public int getCompanyCreditValue() { return COMPANY_CREDITS; }

    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        Location sellerLoc = map.locationOf(seller);
        seller.getInventory().remove(this);

        for (Exit exit : sellerLoc.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround() != null && adj.getGround().toString().contains("Supercomputer")) {
                for (Exit comExit : adj.getExits()) {
                    Location slimeSpawnTile = comExit.getDestination();
                    if (!slimeSpawnTile.containsAnActor() && slimeSpawnTile.canActorEnter(seller)) {
                        spawner.spawnSlime(slimeSpawnTile);
                        return "Stripping the facility's colling system! A Slime immediately crawls out next to the Supercomputer!";
                    }
                }
            }
        }
        return "Ventilation system broken.";
    }

    @Override
    public String depositBy(Actor depositor, GameMap map, QuotaManager quotaManager) {
        depositor.heal(HEAL_AMOUNT);
        return "Company rewards your compliance with a burst of fresh oxygen, healing " + depositor + " for " + HEAL_AMOUNT + " HP!";
    }
}
