package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.TeleportAction;
import game.actors.Undead;
import game.capabilities.CreditHolder;
import game.capabilities.Sellable;
import game.enums.ItemStatistics;
import game.teleportstrategies.AlienCubeStrategy;
import java.util.List;

public class AlienCube extends Item implements Sellable {

    private static final int SELL_PRICE = 25;
    private static final int NUM_OPTIONS = 3;
    private boolean used = false;

    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(0));
        this.makePortable();
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (used) {
            this.makeNonPortable();
            return actions;
        }

        AlienCubeStrategy strategy = new AlienCubeStrategy(map.at(0, 0));

        List<Location> targets = strategy.getRandomDestinations(map, owner, NUM_OPTIONS);
        for (Location loc : targets) {
            actions.add(new TeleportAction(new AlienCubeStrategy(loc)));
        }
        return actions;
    }

    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    public boolean isUsed() {
        return used;
    }

    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        Location sellerLocation = map.locationOf(seller);

        for (Location adjacent : sellerLocation.getNearbyLocations(1)) {
            if (!adjacent.containsAnActor() && adjacent.canActorEnter(seller)) {
                try {
                    Undead undead = new Undead();
                    map.addActor(undead, adjacent);
                    return "An Undead spawns next to " + seller + "!";
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return "No empty tile found. Undead could not spawn.";
    }
}