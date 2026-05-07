package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.grounds.AbstractTree;
import game.teleportstrategies.TreeWarpStrategy;

public class WarperMatureStage extends AbstractTreeStage {

    @Override
    public TreeStage execute(Location location, AbstractTree tree) {
        Actor worker = getNearbyWorker(location);
        if (worker != null) {
            TeleportAction warp = new TeleportAction(new TreeWarpStrategy());
            String result = warp.execute(worker, location.map());
            new Display().println(result);
        }
        return this;
    }

    @Override
    public char getDisplayChar() { return 'W'; }
}
