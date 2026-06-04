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
import game.teleportstrategies.BaseTeleportStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Alien Cube warp space-time, it is an item and can be used as a teleportation device.
 * When carried in a worker's inventory, they can be used to teleport to 3 random locations
 * within the current map. Using them can cause the adjacent ground tiles at the source location into Toxic
 * Waste. The cube can also be sold to the SuperComputer for 25 credits. Once sold, it will
 * instantly spawn an Undead creature on an empty tile directly next to the worker.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class AlienCube extends Item implements Sellable {

    /** Adjacent tile search radius. */
    private static final int ADJACENT_TILES = 1;
    /** Weight of the alien cube in inventory units. */
    private static final int WEIGHT = 1;
    /** Number of credits received when selling the cube. */
    private static final int SELL_PRICE = 25;
    /** Number of random destination options to present to the user. */
    private static final int NUM_OPTIONS = 3;

    /**
     * Constructs a new Alien Cube with default weight and portability.
     */
    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    /**
     * Generates a list of allowable teleport actions for this item.
     * Random valid map locations are generated and presented as teleport options.
     * Duplicate or invalid locations are ignored. A maximum of 3
     * destinations options are shown.
     * @param owner the actor carrying the item
     * @param map the current game map
     * @return a list of teleport actions available to the actor
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        List<Location> chosen = new ArrayList<>();
        int attempts = 0;

        while (chosen.size() < NUM_OPTIONS && attempts < 200) {
            attempts++;
            Location randomLocation = BaseTeleportStrategy.findRandomValidLocation(map, owner);
            if (randomLocation == null) break;
            if (chosen.contains(randomLocation)) continue;

            chosen.add(randomLocation);
            String menuDescription = "Scattered Coordinates at ("
                    + randomLocation.x() + ", " + randomLocation.y() + ")";
            actions.add(new TeleportAction(new AlienCubeStrategy(randomLocation, menuDescription)));
        }

        return actions;
    }

    /**
     * This will return the selling price which is 25 credits.
     * @return sell price
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * This will spawn an undead adjacent to the seller upon sale.
     * @param seller the actor selling the item
     * @param map the current game map
     * @param wallet the seller's credit holder
     * @return the outcome message of whether undead is spawned successfully or not
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        Location sellerLocation = map.locationOf(seller);

        for (Location adjacent : sellerLocation.getNearbyLocations(ADJACENT_TILES)) {
            if (!adjacent.containsAnActor() && adjacent.canActorEnter(seller)) {
                try {
                    Undead undead = new Undead();
                    map.addActor(undead, adjacent);
                    seller.getInventory().remove(this);
                    return "An Undead spawns next to " + seller + "!";
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return "No empty tile found. Undead could not spawn.";
    }
}
