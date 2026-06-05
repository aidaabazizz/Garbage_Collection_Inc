package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.CutAction;
import game.actions.TeleportAction;
import game.actors.Undead;
import game.capabilities.CreditHolder;
import game.capabilities.Cuttable;
import game.capabilities.Sellable;
import game.enums.ItemStatistics;
import game.teleportstrategies.AlienCubeStrategy;
import game.teleportstrategies.BaseTeleportStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Alien Cube warp space-time, it is an item and can be used as a teleportation device.
 * When carried in a worker's inventory, they can be used to teleport to 3 random locations
 * within the current map. Using them can cause the adjacent ground tiles at the source location into Toxic
 * Waste. The cube can also be sold to the SuperComputer for 25 credits. Once sold, it will
 * instantly spawn an Undead creature on an empty tile directly next to the worker.
 *
 * @author Victoria Tay Wen Xie
 * @version 2.0
 */
public class AlienCube extends Item implements Sellable, Cuttable {
    /** Adjacent tile search radius. */
    private static final int ADJACENT_TILES = 1;
    /** Weight of the alien cube in inventory units. */
    private static final int WEIGHT = 1;
    /** Number of credits received when selling the cube. */
    private static final int SELL_PRICE = 25;
    /** Number of random destination options to present to the user. */
    private static final int NUM_OPTIONS = 3;
    /** Number of poison status rounds inflicted on actor. */
    private static final int POISON_ROUNDS = 5;

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
     * @param owner the actor standing next to or on top of the item.
     * @param map the current game map.
     * @return a list of teleport actions available on the floor.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        generateWarpActions(actions, map, owner);
        return actions;
    }

    /**
     * Overrides actions available when the item is tucked inside the actor's inventory bag.
     * DO NOT call generateWarpActions here if the ground method is already running in your engine setup.
     * * @param actor The actor carrying this cube asset.
     * @param location The current map tile location coordinates of the holding actor.
     * @return A consolidated choice list containing only the unique inventory transactions.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location) {
        ActionList actions = new ActionList();
        if (this.canBeCut(actor)) {
            actions.add(new CutAction(this, "Alien Cube", location));
        }
        return actions;
    }

    private void generateWarpActions(ActionList actions, GameMap map, Actor actor) {
        List<Location> chosen = new ArrayList<>();
        int attempts = 0;

        while (chosen.size() < NUM_OPTIONS && attempts < 200) {
            attempts++;
            Location randomLocation = BaseTeleportStrategy.findRandomValidLocation(map, actor);
            if (randomLocation == null) break;
            if (chosen.contains(randomLocation)) continue;

            chosen.add(randomLocation);
            String menuDescription = "Scattered Coordinates at ("
                    + randomLocation.x() + ", " + randomLocation.y() + ")";
            actions.add(new TeleportAction(new AlienCubeStrategy(randomLocation, menuDescription)));
        }
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

    /**
     * Removes itself from the inventory and transform the cut alien cube
     * to the alien artifacts. The consequence is that it inflicts poison status
     * on the worker performing the action.
     *
     * @param actor the active worker executing the tool operation
     * @param map the active game world simulation layout
     * @param targetLocation tracking parameter (passed as null for items handled in inventory)
     * @return contextual operation log output text
     */
    @Override
    public String executeCut(Actor actor, GameMap map, Location targetLocation) {
        actor.getInventory().remove(this);
        AlienArtifact artifact = new AlienArtifact();
        actor.getInventory().add(artifact);
        actor.addStatus(new game.capabilities.PoisonStatus(POISON_ROUNDS));
        return actor + " utilizes Plasma Cutter to destroy the Alien Cube, Alien Artifact added to the inventory! " +
                "Inflicting the poison status on " + actor + " for 5 turns!";
    }
}
