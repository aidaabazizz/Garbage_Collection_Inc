package game.grounds;

import game.managers.Spawner;
import game.stages.FleshySproutStage99;

/**
 * Fleshy Tree variant for the 99-deprecated map.
 * Spawns Undead at sprout stage and ScrapSnatchers at mature stage.
 * Grows to Monolith at final stage which warps workers.
 *
 * @author Aida
 */
public class FleshyTree99 extends AbstractTree {

    public FleshyTree99(Spawner spawner) {
        super("Fleshy Tree (99)", new FleshySproutStage99(spawner));
    }
}