package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

public class AlienCube extends Item {

    public AlienCube() {
        super("Alien Cube", '◈');
    }

    @Override
    public ActionList allowableActions(Actor owner) {
        ActionList actions = new ActionList();
        GameMap map = owner.locationOf(owner).map();

        List<Location> possibleDestinations = new ArrayList<>();

        for (int x : currentMap.getXRange()) {
            for (int y : currentMap.getYRange()) {
                Location loc = currentMap.at(x, y);

                // This single check handles walls, water, and existing actors
                if (loc.canActorEnter(owner)) {
                    possibleDestinations.add(loc);
                }
            }
        }
}
