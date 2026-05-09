package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.TeleportAction;
import game.enums.ItemStatistics;
import game.teleportstrategies.AlienCubeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlienCube extends Item {

    private final static int NUM_LOCATIONS = 3;
    private final static int MAX_ATTEMPT_RANDOM_LOCATIONS = 100;
    private final Random random = new Random();
    private static final int NUM_OPTIONS = 3;

    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(0));
        this.makePortable();
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        // Use BaseTeleportStrategy's robust random location finder
        AlienCubeStrategy tempStrategy = new AlienCubeStrategy(map.at(0, 0));
        List<Location> targets = new ArrayList<>();

        while (targets.size() < NUM_OPTIONS) {
            Location loc = tempStrategy.getRandomValidLocation(map, owner);
            if (loc == null || targets.contains(loc)) continue;
            targets.add(loc);
            actions.add(new TeleportAction(new AlienCubeStrategy(loc)));
        }
        return actions;
    }
}
