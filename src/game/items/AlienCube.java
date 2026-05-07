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
        int maxX = map.getXRange().max();
        int maxY = map.getYRange().max();
        List<Location> targets = new ArrayList<>();

        for (int i = 0; i < NUM_LOCATIONS; i++) {
            Location randomLoc;
            int attempts = 0;
            do {
                int randomX = random.nextInt(maxX + 1);
                int randomY = random.nextInt(maxY + 1);
                randomLoc = map.at(randomX, randomY);
                attempts++;
            } while ((!randomLoc.canActorEnter(owner) || targets.contains(randomLoc)) && attempts < MAX_ATTEMPT_RANDOM_LOCATIONS);

            if (attempts < MAX_ATTEMPT_RANDOM_LOCATIONS) {
                targets.add(randomLoc);
                actions.add(new TeleportAction(new AlienCubeStrategy(randomLoc)));
            }
        }
        return actions;
    }
}
